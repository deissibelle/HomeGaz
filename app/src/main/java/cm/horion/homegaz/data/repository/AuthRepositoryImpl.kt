package cm.horion.homegaz.data.repository

import cm.horion.homegaz.data.datasource.remote.AuthService
import cm.horion.homegaz.domain.model.response.Response
import cm.horion.homegaz.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val authService: AuthService
) : AuthRepository {

    override suspend fun getToken(
        code: String,
        item: String
    ): Response {
        return withContext(Dispatchers.IO){
            try {
                authService.getToken(code,item)
            } catch (e: Exception){
                Response(false,"Serveur injoignable, reesayer plus tard")
            }
        }
    }

    override suspend fun getExchangeToken(): Response? {
        return withContext(Dispatchers.IO){
            try {
                authService.getExchangeToken()
            } catch (e: Exception){
                Response(false,"Serveur injoignable, reesayer plus tard")
            }
        }
    }

    override suspend fun refreshToken(): Response? {
        return withContext(Dispatchers.IO){
            try {
                authService.refreshToken()
            } catch (e: Exception){
                Response(false,"Serveur injoignable, reesayer plus tard")
            }
        }
    }

    override suspend fun logout(): Response? {
        return try {
            val result = authService.logout()
            try {

                if (result == true){
                    Response(true,"logout reussi")
                } else {
                    Response(false,"Erreur ")
                }

            } catch (e: Exception) {
                Response(false,e.message.toString())
            }
        } catch (e: Exception) {
            Response(false,"Erreur de déconnexion: ${e.message}")
        }
    }

    override suspend fun logoutLocal(service : String): Response? {
        return try {
            val result = authService.logoutLocal(service)
            try {
                if (result){
                    Response(true,"logout reussi")
                } else {
                    Response(false,"Erreur ")
                }

            } catch (e: Exception) {
                Response(false,e.message.toString())
            }
        } catch (e: Exception) {
            Response(false,"Erreur de déconnexion: ${e.message}")
        }
    }

}