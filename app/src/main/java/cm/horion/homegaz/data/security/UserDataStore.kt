package cm.horion.homegaz.data.security

import cm.horion.homegaz.domain.model.auth.Token
import kotlinx.coroutines.flow.StateFlow

interface UserDataStore {

    val tokenFlow: StateFlow<String?>
    val authState: StateFlow<AuthState>
    val useFlow: StateFlow<Boolean>
    val tokenExchangeFlow: StateFlow<String?>

    suspend fun getRefreshToken(): String?
    suspend fun getAccessToken(): String?
    suspend fun setAccessToken(newToken: String?)
    suspend fun setRefreshToken(newToken: String?)

    fun saveTokenSettings(token: Token?)
    suspend fun getTokenSettings(): Token?


    suspend fun getExchangeToken(): String?
    suspend fun setExchangeToken(newToken: String?)


    //logique pour voir si user es connecter
    fun onAppStart()
    suspend fun onLoginSuccess(token: Token)
    fun logout()

    fun clear()
}