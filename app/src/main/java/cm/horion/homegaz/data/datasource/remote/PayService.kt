package cm.horion.homegaz.data.datasource.remote

import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import cm.horion.homegaz.data.security.UserDataStore
import cm.horion.homegaz.domain.model.Endpoint
import cm.horion.homegaz.domain.model.consommateur.dto.Profile
import cm.horion.homegaz.domain.model.order.dto.Order
import cm.horion.homegaz.domain.model.order.dto.OrderRequest
import cm.horion.homegaz.domain.model.payment.dto.PaymentRequest
import cm.horion.homegaz.domain.model.payment.dto.SessionsResponse
import cm.horion.homegaz.domain.model.response.Response
import cm.horion.homegaz.util.ApiClient.client
import cm.horion.homegaz.util.Constants.GAZ_URL
import cm.horion.homegaz.util.Constants.PAY_URL
import cm.horion.homegaz.util.appContext
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
import java.util.concurrent.TimeUnit

class PayService(
    private val settingStore : UserDataStore
) {

    suspend fun saveOrder(order : OrderRequest) : Response {
        val token = settingStore.getExchangeToken()
        return try {
            val response: HttpResponse = client.post("$GAZ_URL${Endpoint.Order.path}") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
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

    suspend fun getOrder() : List<Order> {
        val token = settingStore.getExchangeToken()
        return try {
            val response: HttpResponse = client.get("$GAZ_URL${Endpoint.GetDepotGaz.path}") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            return response.body<List<Order>>()

        } catch (e: Exception) {
            return emptyList<Order>()
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
        val token = settingStore.getExchangeToken()
        return try {
            val response: HttpResponse = client.get("$GAZ_URL${Endpoint.Status.path}?sessionUuid=$sessionUuid") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
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

//    fun startTrackingPayment(paymentId: String) {
//        // On impose des contraintes (il faut internet pour vérifier le statut)
//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
//
//        // On prépare les données à envoyer au Worker
//        val paymentData = workDataOf("sessionUuid" to paymentId)
//
//        // On configure le Worker
//        val paymentWorkRequest = OneTimeWorkRequestBuilder<PaymentCheckWorker>()
//            .setInputData(paymentData)
//            .setConstraints(constraints)
//            // STRATÉGIE DES SENIORS : Si le paiement est "PENDING", on réessaie toutes les 10 secondes
//            .setBackoffCriteria(
//                BackoffPolicy.LINEAR,
//                10,
//                TimeUnit.SECONDS
//            )
//            .build()
//
//        // On donne l'ordre à Android d'exécuter la tâche
//        WorkManager.getInstance(appContext).enqueue(paymentWorkRequest)
//    }

    fun startTrackingPayment(paymentId: String) {
        Log.d("PAYEMENT", "lancer 2")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val paymentData = workDataOf("sessionUuid" to paymentId)

        val uniqueObservationTag = "${paymentId}_${System.currentTimeMillis()}"

        val paymentWorkRequest = OneTimeWorkRequestBuilder<PaymentCheckWorker>()
            .setInputData(paymentData)
            .setConstraints(constraints)
            .addTag(paymentId)
            .addTag(uniqueObservationTag)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
            .build()

        // Tu gardes ton fonctionnement UniqueWork pour éviter les doublons de polling
        WorkManager.getInstance(appContext).enqueueUniqueWork(
            paymentId,
            ExistingWorkPolicy.REPLACE,
            paymentWorkRequest
        )
    }


}