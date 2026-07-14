package cm.horion.homegaz.data.security

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cm.horion.homegaz.domain.model.auth.Token
import cm.horion.homegaz.domain.model.auth.isExpiredSoon
import cm.horion.homegaz.domain.model.auth.isTotallyExpired
import cm.horion.homegaz.domain.repository.AuthRepository
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
import kotlin.getValue

class UserDataStoreImpl(
    private val secureStorage: SecureStorage,
    private val dataStore: DataStore<Preferences>
) : UserDataStore, KoinComponent {

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
        // 🎯 Clés uniques et locales bien définies pour éviter les conflits d'importations
        private val TOKEN_EXCHANGE_DATASTORE_KEY = stringPreferencesKey("secure_token_status")
        private const val LOCAL_TOKEN_KEY = "auth_session_tokens"
        private const val LOCAL_EXCHANGE_TOKEN_KEY = "auth_token_exchange"
    }

    override suspend fun getExchangeToken(): String? = withContext(Dispatchers.IO) {
        return@withContext secureStorage.read(LOCAL_EXCHANGE_TOKEN_KEY)
    }

    override suspend fun setExchangeToken(newToken: String?) = withContext(Dispatchers.IO) {
        if (newToken != null) {
            secureStorage.save(LOCAL_EXCHANGE_TOKEN_KEY, newToken)
            dataStore.edit { preferences -> preferences[TOKEN_EXCHANGE_DATASTORE_KEY] = "PRESENT" }
            _tokenExchangeFlow.value = newToken
        } else {
            clearAllData()
        }
    }

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

            // 3. Au moins un des deux jetons a expiré -> Tentative de rafraîchissement
            try {
                var refreshSuccess = true
                var isNetworkError = false

                // Si l'Access Token (30 min) est expiré, on rafraîchit
                if (isAccessExpired) {
                    try {
                        val refreshed = authRepository.refreshToken()
                        if (refreshed != null && refreshed.success ) {
                            //saveTokenSettings(refreshed.token)
                        } else {
                            // Le serveur a répondu mais a explicitement refusé le rafraîchissement (Token banni/invalide)
                            refreshSuccess = false
                        }
                    } catch (e: Exception) {
                        if (e.isNetworkException()) {
                            isNetworkError = true
                        } else {
                            refreshSuccess = false
                        }
                    }
                }

                // Si l'Exchange Token (15 min) est expiré, on rafraîchit
                if (refreshSuccess && !isNetworkError && isExchangeExpired) {
                    try {
                        val newExchangeResponse = authRepository.getExchangeToken()
                        if (newExchangeResponse != null && newExchangeResponse.success && newExchangeResponse.message != null) {
                            setExchangeToken(newExchangeResponse.message)
                        } else {
                            refreshSuccess = false
                        }
                    } catch (e: Exception) {
                        if (e.isNetworkException()) {
                            isNetworkError = true
                        } else {
                            refreshSuccess = false
                        }
                    }
                }

                // 🎯 GESTION DU VERDICT FINAL
                if (isNetworkError) {
                    // ⚠️ Panne réseau : On applique la tolérance hors-ligne
                    android.util.Log.w("AUTH", "Mode hors-ligne détecté pendant le démarrage.")

                    val isAccessTotallyDead = token.isTotallyExpired()
                    val isExchangeTotallyDead = exchangeToken.isRefreshTokenTotallyExpired()

                    if (isAccessTotallyDead || isExchangeTotallyDead) {
                        // Si la date limite absolue du token est dépassée, même sans réseau on déconnecte
                        _authState.value = AuthState.Unauthenticated
                    } else {
                        // Sinon, on le laisse entrer avec ses anciennes données en cache !
                        _tokenFlow.value = token.refreshToken
                        _tokenExchangeFlow.value = exchangeToken
                        _authState.value = AuthState.Authenticated
                    }
                } else if (refreshSuccess) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Unauthenticated
                }

            } catch (e: Exception) {
                android.util.Log.e("AUTH", "Erreur critique d'initialisation : ${e.message}")
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    // 🎯 Petite fonction d'extension bien pratique pour cibler toutes les pannes internet courantes sous Android
    private fun Exception.isNetworkException(): Boolean {
        val message = this.message?.lowercase().orEmpty()
        return this is java.io.IOException ||
                this is java.net.ConnectException ||
                this is java.net.UnknownHostException ||
                this is java.net.SocketTimeoutException ||
                message.contains("connect") ||
                message.contains("timeout") ||
                message.contains("host")
    }

    // 🎯 Sauvegarde les DEUX jetons lors du login réussi
    override suspend fun onLoginSuccess(token: Token) {
        saveTokenSettings(token)

        // On récupère l'exchange token initial suite au login (depuis ton repository d'authentification)
        try {
            val exchangeResponse = authRepository.getExchangeToken()
            if (exchangeResponse != null && exchangeResponse.success ) {
                setExchangeToken(exchangeResponse.message)
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        } catch (e: Exception) {
            android.util.Log.e("AUTH", "Échec récupération exchangeToken suite au login : ${e.message}")
            _authState.value = AuthState.Unauthenticated
        }
    }

    override fun saveTokenSettings(token: Token?) {
        if (token == null) return
        val json = Json.encodeToString(token)
        secureStorage.save(LOCAL_TOKEN_KEY, json)
        _tokenFlow.value = token.refreshToken
    }

    override suspend fun getTokenSettings(): Token? =
        withContext(Dispatchers.IO) {
            secureStorage.read(LOCAL_TOKEN_KEY)?.let {
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
        secureStorage.remove(LOCAL_TOKEN_KEY)
        secureStorage.remove(LOCAL_EXCHANGE_TOKEN_KEY)
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_EXCHANGE_DATASTORE_KEY)
        }
        _tokenFlow.value = null
        _tokenExchangeFlow.value = null

        _authState.value = AuthState.Unauthenticated
    }
}