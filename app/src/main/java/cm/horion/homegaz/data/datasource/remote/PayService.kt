package cm.horion.homegaz.data.datasource.remote

import cm.horion.homegaz.domain.model.Endpoint
import cm.horion.homegaz.domain.model.consommateur.dto.Profile
import cm.horion.homegaz.domain.model.order.dto.OrderRequest
import cm.horion.homegaz.domain.model.payment.dto.PaymentRequest
import cm.horion.homegaz.domain.model.payment.dto.SessionsResponse
import cm.horion.homegaz.domain.model.response.Response
import cm.horion.homegaz.util.ApiClient.client
import cm.horion.homegaz.util.Constants.GAZ_URL
import cm.horion.homegaz.util.Constants.PAY_URL
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class PayService {

    suspend fun saveOrder(order : OrderRequest) : Response {
        return try {
            val response: HttpResponse = client.post("$GAZ_URL${Endpoint.SaveProfile.path}") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(order)
            }

            if (response.status == HttpStatusCode.OK) {
                val responseText = response.bodyAsText()
                val response = Json.decodeFromString<Response>(responseText)
                return Response(true,response.message)
            } else {
                val responseText = response.bodyAsText()
                return Json.decodeFromString<Response>(responseText)
            }

        } catch (e : Exception) {
            Response(
                success = false,
                message = e.message ?: "Erreur réseau"
            )
        }
    }

    suspend fun payement(pay: PaymentRequest): Response {
        return try {
            val response: HttpResponse = client.post("$PAY_URL${Endpoint.Payment.path}") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(pay)
            }

            return when (response.status) {
                HttpStatusCode.OK,
                HttpStatusCode.Created,
                HttpStatusCode.BadRequest,
                HttpStatusCode.TooManyRequests-> {
                    response.body<Response>()
                }
                else ->
                    Response(
                        success = false,
                        message = "Erreur serveur (${response.status})"
                    )
            }
        } catch (e: Exception) {
            Response(
                success = false,
                message = e.message ?: "Erreur réseau"
            )
        }
    }

    suspend fun getCvStatus(sessionUuid: String): SessionsResponse {
        return try {
            val response: HttpResponse = client.get("$GAZ_URL${Endpoint.Status.path}?sessionUuid=$sessionUuid") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer token")
                }

            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    response.body<SessionsResponse>()
                }
                HttpStatusCode.BadRequest ->{
                    response.body<SessionsResponse>()
                }

                HttpStatusCode.Unauthorized ->
                    SessionsResponse(
                        success = false,
                        message = "Non autorisé"
                    )

                else ->
                    SessionsResponse(
                        success = false,
                        message = "Erreur serveur (${response.status})"
                    )
            }
        } catch (e: Exception) {
            SessionsResponse(
                success = false,
                message = e.message ?: "Erreur réseau"
            )
        }
    }


}