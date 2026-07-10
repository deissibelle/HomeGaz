package cm.horion.homegaz.data.datasource.remote

import android.util.Log
import cm.horion.homegaz.data.security.UserDataStore
import cm.horion.homegaz.domain.model.Endpoint
import cm.horion.homegaz.domain.model.auth.RefreshTokenRequest
import cm.horion.homegaz.domain.model.auth.Token
import cm.horion.homegaz.domain.model.response.Response
import cm.horion.homegaz.util.ApiClient.client
import cm.horion.homegaz.util.Constants.AUTH_API_URL
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Error(val error : String,val attemptsRemaining: String? = null)

@Serializable
data class ExchangeTokenResponse(
    val accessToken: String
)

class AuthService(
    private val settingStore : UserDataStore
) {

    suspend fun getToken(code: String,item: String) : Response {
        val response: HttpResponse = client.post("$AUTH_API_URL${Endpoint.Token.path}?code=$code&item=$item") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
        val responseText = response.bodyAsText()
        return if (response.status == HttpStatusCode.OK) {
            val token = Json.decodeFromString<Token>(responseText)
            Log.d("CONNEXION",token.toString())
            settingStore.onLoginSuccess(token)
            Response(success = true, message = "Connexion reussi")
        } else {
            val res = Json.decodeFromString<Error>(responseText)
            Response(success = false, message = res.error)
        }
    }

    suspend fun getExchangeToken() : Response? {
        val token = settingStore.getAccessToken()
        val response: HttpResponse = client.get("$AUTH_API_URL${Endpoint.Exchange.path}") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            url {
                parameters.append("service", "GAZ")
            }
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
                append("X-Client-Type", "MOBILE")
            }
        }
        val responseText = response.bodyAsText()
        return if (response.status == HttpStatusCode.OK) {
            val res = Json.decodeFromString<ExchangeTokenResponse>(responseText)
            settingStore.setExchangeToken(res.accessToken)
            Response(success = true, message = res.accessToken)
        } else  {
            val res = Json.decodeFromString<Error>(responseText)
            Response(success = false, message = res.error)
        }
    }

    suspend fun refreshToken(): Response? {
        val token = settingStore.getRefreshToken() ?: return null
        val response: HttpResponse = client.post("$AUTH_API_URL${Endpoint.RefreshToken.path}") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            headers {
                append("X-Client-Type", "MOBILE")
            }
            setBody(RefreshTokenRequest(token))
        }
        val responseText = response.bodyAsText()
        return try {
            when (response.status) {
                HttpStatusCode.OK -> {
                    val token = Json.decodeFromString<Token>(responseText)
                    settingStore.onLoginSuccess(token)
                    Response(success = true, message = "refresh")
                }
                HttpStatusCode.InternalServerError -> {
                    val res = Json.decodeFromString<Error>(responseText)
                    Response(success = false, message = res.error)
                }
                else -> {
                    null
                }
            }
        } catch (e: Exception) {
            Response(success = false, message = "erreur serveur")
        }
    }

}