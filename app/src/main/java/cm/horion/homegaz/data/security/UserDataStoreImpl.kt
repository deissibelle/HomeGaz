package cm.horion.homegaz.data.security

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cm.horion.homegaz.domain.model.auth.Token
import cm.horion.homegaz.domain.model.auth.isExpiredSoon
import cm.horion.homegaz.domain.model.auth.isTotallyExpired
import cm.horion.homegaz.domain.repository.AuthRepository
import cm.horion.homegaz.util.Constants.TOKEN_KEY
import cm.horion.homegaz.util.isExchangeExpiredSoon
import cm.horion.homegaz.util.isRefreshTokenTotallyExpired
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue

class UserDataStoreImpl(
    private val secureStorage: SecureStorage,
    private val dataStore: DataStore<Preferences>
) : UserDataStore , KoinComponent {

    private val _tokenFlow = MutableStateFlow<String?>(null)
    override val tokenFlow: StateFlow<String?> = _tokenFlow

    private val _tokenExchangeFlow = MutableStateFlow<String?>(null)
    override val tokenExchangeFlow: StateFlow<String?> = _tokenExchangeFlow

    private val _authState = MutableStateFlow<AuthState>(AuthState.Checking)
    override val authState: StateFlow<AuthState> = _authState

    private val _useFlow = MutableStateFlow(false)
    override val useFlow: StateFlow<Boolean> = _useFlow

    private val scope = CoroutineScope(Dispatchers.IO)
    private val authRepository: AuthRepository by inject()
    companion object {
        private val TOKEN_EXCHANGE_KEY = stringPreferencesKey("secure_token")
        private const val TOKEN_KEY = "auth_session_tokens"
        private const val EXCHANGE_TOKEN_KEY = "auth_token"
    }

    override suspend fun getExchangeToken(): String? = withContext(Dispatchers.IO) {
        return@withContext secureStorage.read(EXCHANGE_TOKEN_KEY)
    }

    override suspend fun setExchangeToken(newToken: String?) = withContext(Dispatchers.IO) {
        if (newToken != null) {
            secureStorage.save(EXCHANGE_TOKEN_KEY, newToken)
            dataStore.edit { preferences -> preferences[TOKEN_EXCHANGE_KEY] = "PRESENT" }
            _tokenExchangeFlow.value = newToken
        } else {
            clearAllData()
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // 💡 LOGIQUE ON_APP_START CORRIGÉE ET MISE À JOUR
    // ─────────────────────────────────────────────────────────────────
    override fun onAppStart() {
        scope.launch(Dispatchers.IO) {
            val token = getTokenSettings()
            val exchangeToken = getExchangeToken()

            // 1. Aucun jeton trouvé -> Non authentifié d'office
            if (token == null || exchangeToken == null) {
                _authState.value = AuthState.Unauthenticated
                return@launch
            }

            // 2. Les deux sont encore valides -> Authentifié directement
            val isAccessExpired = token.isExpiredSoon()
            val isExchangeExpired = exchangeToken.isExchangeExpiredSoon()

            if (!isAccessExpired && !isExchangeExpired) {
                _tokenFlow.value = token.refreshToken
                _tokenExchangeFlow.value = exchangeToken
                _authState.value = AuthState.Authenticated
                return@launch
            }

            // 3. Au moins un des deux jetons a expiré -> Stratégie de rafraîchissement ciblée
            try {
                var refreshSuccess = true

                // Si l'Access Token (30 min) est expiré, on rafraîchit la session API principale
                if (isAccessExpired) {
                    val refreshed = authRepository.refreshToken()
                    if (refreshed != null && refreshed.success) {
                        // 💡 TRÈS IMPORTANT : Ici, sauvegarde le nouveau Token d'API renvoyé par ton serveur
                        // ex: saveTokenSettings(refreshed.token)
                    } else {
                        refreshSuccess = false
                    }
                }

                // Si l'Exchange Token (15 min) est expiré, on demande un nouveau au serveur
                // Note : On ne le fait que si le refresh précédent (si applicable) a fonctionné
                if (refreshSuccess && isExchangeExpired) {
                    val newExchangeResponse = authRepository.getExchangeToken()
                    if (newExchangeResponse != null && newExchangeResponse.success) {
                        // 💡 TRÈS IMPORTANT : Sauvegarde le nouvel exchange token reçu en base locale !
                        // ex: newExchangeResponse.exchangeToken?.let { setExchangeToken(it) }
                    } else {
                        refreshSuccess = false
                    }
                }

                if (refreshSuccess) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Unauthenticated
                }

            } catch (e: java.io.IOException) {
                // ⚠️ Panne réseau : Gestion de la tolérance hors-ligne
                android.util.Log.e("AUTH", "Pas d'internet pour le refresh asynchrone : ${e.message}")

                // On vérifie la date de mort absolue (sans la marge des 5 min)
                val isAccessTotallyDead = token.isTotallyExpired() // Implémente cette extension sur ton objet Token
                val isExchangeTotallyDead = exchangeToken.isRefreshTokenTotallyExpired() // Vérifie si le JWT string est expiré à 100%

                if (isAccessTotallyDead || isExchangeTotallyDead) {
                    // Si l'un des deux est complètement expiré et qu'on n'a pas de réseau -> Déconnexion
                    _authState.value = AuthState.Unauthenticated
                } else {
                    // Mode hors-ligne toléré : ils entrent dans leur fenêtre de fin mais le réseau est absent
                    _tokenFlow.value = token.refreshToken
                    _tokenExchangeFlow.value = exchangeToken
                    _authState.value = AuthState.Authenticated
                }
            } catch (e: Exception) {
                android.util.Log.e("AUTH", "Erreur critique d'initialisation : ${e.message}")
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    override suspend fun onLoginSuccess(token: Token) {
        saveTokenSettings(token)
        _authState.value = AuthState.Authenticated
    }

    override fun saveTokenSettings(token: Token?) {
        if (token == null) return
        val json = Json.encodeToString(token)
        secureStorage.save(TOKEN_KEY, json)
        _tokenFlow.value = token.refreshToken
    }

    override suspend fun getTokenSettings(): Token? =
        withContext(Dispatchers.IO) {
            secureStorage.read(TOKEN_KEY)?.let {
                runCatching { Json.decodeFromString<Token>(it) }.getOrNull()
            }
        }

    override suspend fun getAccessToken(): String? = getTokenSettings()?.accessToken

    override suspend fun getRefreshToken(): String? = getTokenSettings()?.refreshToken

    override suspend fun setAccessToken(newToken: String?) {
        val current = getTokenSettings() ?: return
        saveTokenSettings(current.copy(accessToken = newToken.orEmpty()))
    }

    override suspend fun setRefreshToken(newToken: String?) {
        val current = getTokenSettings() ?: return
        saveTokenSettings(current.copy(refreshToken = newToken.orEmpty()))
    }

    override fun logout() {
        scope.launch { clearAllData() }
    }

    override fun clear() {
        scope.launch { clearAllData() }
    }

    private suspend fun clearAllData() {
        secureStorage.remove(TOKEN_KEY)
        secureStorage.remove(EXCHANGE_TOKEN_KEY)
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_EXCHANGE_KEY)
        }
        _tokenFlow.value = null
        _tokenExchangeFlow.value = null

        _authState.value = AuthState.Unauthenticated
    }
}