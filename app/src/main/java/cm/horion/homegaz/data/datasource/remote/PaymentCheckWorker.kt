package cm.horion.homegaz.data.datasource.remote

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import cm.horion.homegaz.data.security.UserDataStore
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
    workerParams: WorkerParameters,
    private val authRepository : UserDataStore
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // 🛠️ Note : Assure-toi que la clé lue correspond à celle injectée (ex: "sessionUuid")
        val paymentId = inputData.getString("sessionUuid") ?: return Result.failure()
        val token = authRepository.getExchangeToken()

        return try {
            val response: HttpResponse = client.get("$GAZ_URL${Endpoint.Status.path}?sessionUuid=$paymentId") {
                accept(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }

            val responseText = response.bodyAsText()
            val session = Json.decodeFromString<SessionsResponse>(responseText)

            when (session.status.name) {
                "SUCCESS" -> {
                    showNotification("Paiement Réussi", "Votre commande a été validée avec succès.")
                    Result.success()
                }
                "FAILED" -> {
                    showNotification("Échec du paiement", session.message)
                    // 🔥 On injecte le message de l'API dans les données de sortie de l'échec
                    val outputData = workDataOf("error_message" to session.message)
                    Result.failure(outputData)
                }
                else -> {
                    Result.retry() // PENDING ou autre : le WorkManager planifie un nouveau call
                }
            }
        } catch (e: Exception) {
            val errorData = workDataOf("error_message" to (e.localizedMessage ?: "Erreur réseau inconnue"))
            Result.retry()
        }
    }


    private fun showNotification(title: String, message: String) {
        // Code standard Android pour afficher une notification dans la barre d'état
        // (Nécessite la création préalable d'un Notification Channel)
    }

}