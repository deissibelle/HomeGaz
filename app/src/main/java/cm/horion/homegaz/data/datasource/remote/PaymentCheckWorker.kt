package cm.horion.homegaz.data.datasource.remote

import android.content.Context
import android.util.Log
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
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import cm.horion.homegaz.R
import cm.horion.homegaz.util.appContext


class PaymentCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    //private val authRepository : UserDataStore
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("PAYEMENT", "lancer 3")
        // 🛠️ Note : Assure-toi que la clé lue correspond à celle injectée (ex: "sessionUuid")
        val paymentId = inputData.getString("sessionUuid") ?: return Result.failure()
        //val token = authRepository.getExchangeToken()

        Log.d("PAYEMENT", paymentId)

        return try {
            val response: HttpResponse = client.get("$GAZ_URL${Endpoint.Status.path}?sessionUuid=$paymentId") {
                accept(ContentType.Application.Json)
//                headers {
//                    append(HttpHeaders.Authorization, "Bearer $token")
//                }
            }

            val responseText = response.bodyAsText()
            Log.d("PAYEMENT", "Réponse brute API: $responseText") // 👈 Ajoute ce log
            val session = Json.decodeFromString<SessionsResponse>(responseText)

            when (session.status.name) {
                "SUCCESS" -> {
                    showNotification("Paiement Réussi", "Votre commande a été validée.")
                    Result.success()
                }
                "FAILED" -> {
                    val outputData = workDataOf("error_message" to session.message)
                    showNotification("Paiement Echoue", outputData.toString())
                    Result.failure(outputData)
                }
                else -> {
                    Log.d("PAYEMENT", "Statut en attente (${session.status.name}), retry...")
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Log.e("PAYEMENT", "Le Worker a crashé !", e) // 👈 Vois le vrai crash ici !
            // On passe en failure explicite avec l'erreur pour que ton ViewModel l'affiche sur l'UI
            val errorData = workDataOf("error_message" to (e.localizedMessage ?: "Erreur réseau inconnue"))
            Result.failure(errorData)
        }
    }


    private fun showNotification(title: String, message: String) {
        val channelId = "payment_status_channel"
        val notificationId = 1001

        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // ✅ Vérifie si les notifications sont activées
        Log.d("PAYEMENT", "areNotificationsEnabled: ${notificationManager.areNotificationsEnabled()}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Suivi des Paiements",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications liées au statut des paiements"
            }
            notificationManager.createNotificationChannel(channel)

            // ✅ Vérifie que le channel n'est pas bloqué
            val createdChannel = notificationManager.getNotificationChannel(channelId)
            Log.d("PAYEMENT", "Channel importance: ${createdChannel?.importance}")
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        Log.d("PAYEMENT", "notify() appelé")
        notificationManager.notify(notificationId, notification)
        Log.d("PAYEMENT", "notify() terminé")
    }

}