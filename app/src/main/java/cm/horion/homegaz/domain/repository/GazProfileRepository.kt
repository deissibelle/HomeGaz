package cm.horion.homegaz.domain.repository


import android.content.Context
import android.content.SharedPreferences
import cm.horion.homegaz.domain.model.consommateur.dto.Profile
import cm.horion.homegaz.domain.model.consommateur.request.ProfileRequest
import cm.horion.homegaz.domain.model.gazprofile.GazProfile
import kotlinx.serialization.json.Json

class GazProfileRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("gaz_profile_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PROFILE_JSON = "gaz_profile_json"
    }

    fun save(profile: Profile) {
        val jsonString = Json.encodeToString(profile)
        prefs.edit().putString(KEY_PROFILE_JSON, jsonString).apply()
    }

    fun load(): Profile? {
        val jsonString = prefs.getString(KEY_PROFILE_JSON, null) ?: return null
        return try {
            Json.decodeFromString<Profile>(jsonString)
        } catch (e: Exception) {
            null
        }
    }

    fun clear() = prefs.edit().clear().apply()
}