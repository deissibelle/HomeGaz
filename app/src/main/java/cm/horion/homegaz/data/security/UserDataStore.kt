package cm.horion.homegaz.data.security

import kotlinx.coroutines.flow.StateFlow

interface UserDataStore {

    val tokenFlow: StateFlow<String?>

    suspend fun getExchangeToken(): String?
    suspend fun setExchangeToken(newToken: String?)

    //logique pour voir si user es connecter
    fun onAppStart()
    fun onLoginSuccess(token: String)
    fun logout()

    fun clear()
}