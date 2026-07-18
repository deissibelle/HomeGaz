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
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class PaymentCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    //private val authRepository : UserDataStore
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val authRepository : UserDataStore by inject()

    override suspend fun doWork(): Result {
        // 🎯 Étape 1 : Log immédiat dès l'entrée pour valider que le Worker tourne !
        Log.d("PAYEMENT", "=== [WORKER] doWork() démarré à la milliseconde ===")

        val paymentId = inputData.getString("sessionUuid") ?: return Result.failure()
        Log.d("PAYEMENT", "[WORKER] Suivi du paymentId : $paymentId")

        // Récupération du token depuis le DataStore
        val token = authRepository.getExchangeToken()
        Log.d("PAYEMENT", "[WORKER] Token récupéré avec succès")

        var maxAttempts = 15 // Exemple : 15 tentatives max
        var currentAttempt = 0

        // 🎯 Boucle de polling interne pour éviter de détruire/recréer le Worker à chaque seconde
        while (currentAttempt < maxAttempts) {
            try {
                currentAttempt++
                Log.d("PAYEMENT", "[WORKER] Tentative d'appel API #$currentAttempt...")

                val response: HttpResponse = client.get("$GAZ_URL${Endpoint.Status.path}?sessionUuid=$paymentId") {
                    accept(ContentType.Application.Json)
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }

                val responseText = response.bodyAsText()
                Log.d("PAYEMENT", "[WORKER] Réponse brute API: $responseText")

                val session = Json.decodeFromString<SessionsResponse>(responseText)

                when (session.status.name) {
                    "SUCCESS" -> {
                        showNotification("Paiement Réussi", "Votre commande a été validée.")
                        return Result.success()
                    }
                    "FAILED" -> {
                        val outputData = workDataOf("error_message" to session.message)
                        showNotification("Paiement Échoué", session.message)
                        return Result.failure(outputData)
                    }
                    else -> {
                        Log.d("PAYEMENT", "[WORKER] Statut toujours en traitement (${session.status.name}).")
                    }
                }
            } catch (e: Exception) {
                Log.e("PAYEMENT", "[WORKER] Erreur lors de la tentative #$currentAttempt", e)
                // Si c'est un crash réseau pur lors d'un appel, on ne lâche pas l'affaire tout de suite, on attend la prochaine boucle
            }

            // Attendre 4 secondes avant la prochaine vérification de statut
            delay(4000)
        }

        // Si on arrive ici, c'est que le paiement a expiré ou a pris trop de temps (Timeout)
        Log.w("PAYEMENT", "[WORKER] Fin du tracking : Temps d'attente dépassé (Timeout).")
        return Result.failure(workDataOf("error_message" to "Temps d'attente dépassé"))
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