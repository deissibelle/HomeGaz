package cm.horion.homegaz.data.security

import android.util.Base64
import org.json.JSONObject

object JwtHelper {
    fun getExpirationDate(token: String): Long? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null

            // Décodage de la partie centrale (Payload)
            val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
            val jsonObject = JSONObject(payload)

            // "exp" est en secondes, on convertit en millisecondes
            jsonObject.getLong("exp") * 1000
        } catch (e: Exception) {
            null
        }
    }
}