package cm.horion.homegaz.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import cm.horion.homegaz.data.security.JwtHelper
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


lateinit var appContext: Context
    private set

fun initSettings(context: Context) {
    appContext = context.applicationContext
}

@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(): android.location.Location? =
    kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
        val fusedClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(appContext)
        var locationCallback: com.google.android.gms.location.LocationCallback? = null

        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                if (continuation.isActive) continuation.resume(location) {}
                return@addOnSuccessListener
            }

            val request = com.google.android.gms.location.LocationRequest.Builder(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 3000L
            ).setMaxUpdates(1).build()

            locationCallback = object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                    locationCallback?.let { fusedClient.removeLocationUpdates(it) }
                    if (continuation.isActive) continuation.resume(result.lastLocation) {}
                }
            }

            fusedClient.requestLocationUpdates(request, locationCallback!!, android.os.Looper.getMainLooper())
                .addOnFailureListener { if (continuation.isActive) continuation.resume(null) {} }
        }.addOnFailureListener { if (continuation.isActive) continuation.resume(null) {} }

        continuation.invokeOnCancellation {
            locationCallback?.let { fusedClient.removeLocationUpdates(it) }
        }
    }

//@RequiresPermission(anyOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
//suspend fun getCurrentLocation(): Location? =
//    suspendCancellableCoroutine { continuation ->
//
//        val fusedClient = LocationServices.getFusedLocationProviderClient(appContext)
//
//        // On crée le callback ici pour pouvoir le détacher en cas d'annulation de la Coroutine
//        var locationCallback: LocationCallback? = null
//
//        fusedClient.lastLocation
//            .addOnSuccessListener { location ->
//                if (location != null) {
//                    if (continuation.isActive) continuation.resume(location) {}
//                    return@addOnSuccessListener
//                }
//
//                // Si le cache est vide, on configure la demande active
//                val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
//                    .setMaxUpdates(1) // S'arrête dès qu'une coordonnée est trouvée
//                    .build()
//
//                locationCallback = object : LocationCallback() {
//                    override fun onLocationResult(result: LocationResult) {
//                        // Nettoyage immédiat dès qu'on a le résultat
//                        locationCallback?.let { fusedClient.removeLocationUpdates(it) }
//
//                        if (continuation.isActive) {
//                            continuation.resume(result.lastLocation) {}
//                        }
//                    }
//                }
//
//                fusedClient.requestLocationUpdates(
//                    request,
//                    locationCallback!!,
//                    Looper.getMainLooper()
//                ).addOnFailureListener {
//                    // Si l'inscription échoue (ex: GPS désactivé), on débloque la coroutine
//                    if (continuation.isActive) continuation.resume(null) {}
//                }
//            }
//            .addOnFailureListener {
//                if (continuation.isActive) continuation.resume(null) {}
//            }
//
//        // 🔥 LA SÉCURITÉ : Si la Coroutine est annulée par l'UI, on force le retrait du callback
//        continuation.invokeOnCancellation {
//            locationCallback?.let { callback ->
//                fusedClient.removeLocationUpdates(callback)
//            }
//        }
//    }

enum class OperatorNetwork {
    ORANGE,
    MTN,
    CAMTEL,
    INVALID
}

fun getOperator(num: String): OperatorNetwork {
    // Nettoyer les espaces ou indicatifs si nécessaire (ex: +237)
    val cleanNum = num.replace("\\s+".toRegex(), "").takeLast(9)

    return when {
        // Orange : 655–659, 685–689, 690–699
        "^6(5[5-9]|8[5-9]|9[0-9])[0-9]{6}$".toRegex().matches(cleanNum) -> OperatorNetwork.ORANGE

        // MTN : 650–654, 670–679, 680–684
        "^6(5[0-4]|7[0-9]|8[0-4])[0-9]{6}$".toRegex().matches(cleanNum) -> OperatorNetwork.MTN

        // Camtel : Généralement les numéros commençant par 242, 243, 620...
        "^2(4[2-3])[0-9]{6}$".toRegex().matches(cleanNum) -> OperatorNetwork.CAMTEL

        // Sinon le numéro n'est pas conforme au plan de numérotation à 9 chiffres du Cameroun
        else -> OperatorNetwork.INVALID
    }
}

fun isPaymentMethodValid(phoneNumber: String, method: PaymentMethod): Boolean {
    val operator = getOperator(phoneNumber)

    return when (method) {
        PaymentMethod.OM -> operator == OperatorNetwork.ORANGE
        PaymentMethod.MOMO -> operator == OperatorNetwork.MTN
    }
}

fun phoneErrorMessage(phone: String, method: PaymentMethod): String? {
    if (phone.isBlank()) return null // Pas d'erreur si le champ est vide

    // Extrait uniquement les chiffres et prend les 9 derniers (élimine +237, les espaces, etc.)
    val normalized = phone.replace("\\s+".toRegex(), "").takeLast(9)

    // 1. Vérification de la longueur globale
    if (normalized.length != 9 || !normalized.all { it.isDigit() }) {
        return "Numéro invalide (9 chiffres requis)"
    }

    // 2. Vérification de la conformité avec l'opérateur sélectionné
    val isValidForMethod = isPaymentMethodValid(normalized, method)

    return if (!isValidForMethod) {
        when (method) {
            PaymentMethod.OM -> "Ce numéro n'est pas un numéro Orange"
            PaymentMethod.MOMO -> "Ce numéro n'est pas un numéro MTN"
        }
    } else {
        null // Aucune erreur, le numéro est parfait !
    }
}

fun String.getUuidFromToken(): String? {
    return JwtHelper.getUuid(this)
}

fun String.isExchangeExpiredSoon(): Boolean {
    val expirationTime = JwtHelper.getExpirationDate(this) ?: return true
    val bufferTime = 5 * 60 * 1000 // 5 minutes
    return (System.currentTimeMillis() + bufferTime) >= expirationTime
}

fun String.isRefreshTokenTotallyExpired(): Boolean {
    val expirationTime = JwtHelper.getExpirationDate(this) ?: return true
    return System.currentTimeMillis() >= expirationTime
}

fun String.getDateOnly(
    pattern: String = "yyyy-MM-dd",
    zoneId: ZoneId = ZoneId.systemDefault()
): String {
    return try {
        val instant = Instant.parse(this)
        val localDateTime = instant.atZone(zoneId)
        val formatter = DateTimeFormatter.ofPattern(pattern)
        localDateTime.format(formatter)
    } catch (e: Exception) {
        "" // Retourne une chaîne vide en cas de format invalide
    }
}


fun String.getTimeOnly(
    pattern: String = "HH:mm:ss",
    zoneId: ZoneId = ZoneId.systemDefault()
): String {
    return try {
        val instant = Instant.parse(this)
        val localDateTime = instant.atZone(zoneId)
        val formatter = DateTimeFormatter.ofPattern(pattern)
        localDateTime.format(formatter)
    } catch (e: Exception) {
        ""
    }
}


