package cm.horion.homegaz.domain.model.auth

import cm.horion.homegaz.data.security.JwtHelper
import kotlinx.serialization.Serializable

@Serializable
data class Token(
    val accessToken: String,
    val refreshToken: String
)

fun Token.isExpiredSoon(): Boolean {
    val expirationTime = JwtHelper.getExpirationDate(this.accessToken) ?: return true

    // Marge de sécurité de 5 minutes (300 000 ms)
    val bufferTime = 5 * 60 * 1000

    // Si (Maintenant + 5min) est plus grand que la date d'expiration, on rafraîchit
    return (System.currentTimeMillis() + bufferTime) >= expirationTime
}

fun Token.isTotallyExpired(): Boolean {
    val expirationTime = JwtHelper.getExpirationDate(this.refreshToken) ?: return false

    return System.currentTimeMillis() >= expirationTime
}