package cm.horion.homegaz.domain.repository

import cm.horion.homegaz.domain.model.response.Response

interface AuthRepository {
    suspend fun getToken(code: String,item: String) : Response
    suspend fun getExchangeToken() : Response?
    suspend fun refreshToken(): Response?
}