package cm.horion.homegaz.data.datasource.remote

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cm.horion.homegaz.domain.model.Endpoint
import cm.horion.homegaz.domain.model.payment.dto.SessionsResponse
import cm.horion.homegaz.domain.model.response.Response
import cm.horion.homegaz.util.ApiClient.client
import cm.horion.homegaz.util.Constants.GAZ_URL
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json


class PaymentCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val paymentId = inputData.getString("sessionUuid") ?: return Result.failure()

        return try {
            val response: HttpResponse = client.get("$GAZ_URL${Endpoint.Status.path}?sessionUuid=$paymentId") {
                accept(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer token")
                }
            }

            val responseText = response.bodyAsText()
            val status = Json.decodeFromString<SessionsResponse>(responseText)
            when (status.status.name) {
                "SUCCESS" -> {
                    showNotification("Paiement Réussi", "Votre commande a été validée avec succès.")
                    Result.success()
                }
                "FAILED" -> {
                    showNotification("Échec du paiement", "Le paiement a été refusé.")
                    Result.failure()
                }
                else -> {
                    // Toujours "PENDING" : on demande à WorkManager de retenter selon la BackoffPolicy
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            // Erreur réseau, timeout ou problème Ktor : on retente plus tard
            Result.retry()
        }
    }


    private fun showNotification(title: String, message: String) {
        // Code standard Android pour afficher une notification dans la barre d'état
        // (Nécessite la création préalable d'un Notification Channel)
    }

}