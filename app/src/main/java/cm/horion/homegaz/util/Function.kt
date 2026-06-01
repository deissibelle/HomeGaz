package cm.horion.homegaz.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
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


