package cm.horion.homegaz.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await


lateinit var appContext: Context
    private set

fun initSettings(context: Context) {
    appContext = context.applicationContext
}

@SuppressLint("MissingPermission") // On part du principe que la permission est gérée en amont
suspend fun getCurrentLocation(): Location? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)

    return try {
        // On demande la position avec une haute précision
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null // Un CancellationToken peut être ajouté ici pour annuler la requête si besoin
        ).await() // .await() nécessite la dépendance 'kotlinx-coroutines-play-services'
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}