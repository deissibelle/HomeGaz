package cm.horion.homegaz.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull


lateinit var appContext: Context
    private set

fun initSettings(context: Context) {
    appContext = context.applicationContext
}

@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(): Location? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)

    return try {
        // ⏱️ STRATÉGIE 1 : On limite l'attente à 4 secondes max pour ne pas bloquer l'expérience utilisateur
        withTimeoutOrNull(4000L) {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()
        } ?: run {
            // 🔄 STRATÉGIE 2 : Si le Timeout a expiré (retourne null), on tente instantanément le cache
            println("⚠️ Connexion faible ou instable. Tentative de récupération de la dernière position connue...")
            fusedLocationClient.lastLocation.await()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        // 🔍 Si tout a échoué, on tente quand même une ultime fois le cache au cas où l'erreur venait uniquement du fix direct
        runCatching { fusedLocationClient.lastLocation.await() }.getOrNull()
    }
}