package cm.horion.homegaz.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import cm.horion.homegaz.data.security.JwtHelper
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull


lateinit var appContext: Context
    private set

fun initSettings(context: Context) {
    appContext = context.applicationContext
}

@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
suspend fun getCurrentLocation(): Location? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)

    return try {
        // 🚀 STRATÉGIE 1 : On tente le cache d'abord. C'est INSTANTANÉ (0 milliseconde).
        val cachedLocation = fusedLocationClient.lastLocation.await()

        // Optionnel : Vérifier si la position n'est pas trop vieille (ex: moins de 5 minutes)
        if (cachedLocation != null && (System.currentTimeMillis() - cachedLocation.time) < 5 * 60 * 1000) {
            return cachedLocation
        }

        // ⏱️ STRATÉGIE 2 : Si pas de cache (ou trop vieux), on demande une position fraîche mais avec un timeout plus court
        withTimeoutOrNull(2500L) { // 2.5 secondes suffisent généralement pour un fix réseau/Wi-Fi
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()
        } ?: cachedLocation // Fallback ultime sur le cache même s'il est vieux, plutôt que de renvoyer null

    } catch (e: Exception) {
        e.printStackTrace()
        // En cas de crash de l'API Google, tentative de secours finale sur le cache
        runCatching { fusedLocationClient.lastLocation.await() }.getOrNull()
    }
}

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

fun String.isExpiredSoon(): Boolean {
    val expirationTime = JwtHelper.getExpirationDate(this) ?: return true

    // Marge de sécurité de 5 minutes (300 000 ms)
    val bufferTime = 5 * 60 * 1000

    // Si (Maintenant + 5min) est plus grand que la date d'expiration, on rafraîchit
    return (System.currentTimeMillis() + bufferTime) >= expirationTime
}


