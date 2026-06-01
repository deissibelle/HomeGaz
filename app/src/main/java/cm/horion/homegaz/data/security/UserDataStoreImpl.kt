package cm.horion.homegaz.data.security

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cm.horion.homegaz.util.isExpiredSoon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserDataStoreImpl(
    private val secureStorage: SecureStorage,
    private val dataStore: DataStore<Preferences>
) : UserDataStore {

    private val _tokenFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    override val tokenFlow: StateFlow<String?> = _tokenFlow

    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("secure_token")
    }

    override suspend fun getExchangeToken(): String? = withContext(Dispatchers.IO) {
        // Récupération directe et sécurisée
        return@withContext secureStorage.read("auth_token")
    }

    override suspend fun setExchangeToken(newToken: String?) = withContext(Dispatchers.IO) {
        if (newToken != null) {
            secureStorage.save("auth_token", newToken)
            dataStore.edit { preferences -> preferences[TOKEN_KEY] = "PRESENT" }
            _tokenFlow.value = newToken
        } else {
            clearAllData()
        }
    }

    override fun onAppStart() {
        scope.launch {
            // Au démarrage, on va lire le stockage sécurisé en tâche de fond
            // pour alimenter instantanément notre Flow d'authentification
            val savedToken = getExchangeToken()
            _tokenFlow.value = savedToken
        }
    }

    override fun onLoginSuccess(token: String) {
        scope.launch {
            setExchangeToken(token)
        }
    }

    override fun logout() {
        scope.launch {
            clearAllData()
        }
    }

    override fun clear() {
        scope.launch {
            clearAllData()
        }
    }

    // Centralisation du nettoyage pour éviter les répétitions de code
    private suspend fun clearAllData() {
        secureStorage.remove("auth_token")
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
        _tokenFlow.value = null
    }

//    private suspend fun refreshIfNecessary(currentToken: String): Boolean =
//        withContext(Dispatchers.IO) {
//            if (!currentToken.isExpiredSoon()) return@withContext true
//
//            return@withContext try {
//                val response = authRepository.refreshToken()
//                response != null && response.success
//            } catch (e: Exception) {
//                false
//            }
//        }

}