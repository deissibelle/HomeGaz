package cm.horion.homegaz.domain.repository


import android.content.Context
import android.content.SharedPreferences
import cm.horion.homegaz.domain.model.gazprofile.GazProfile

class GazProfileRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("gaz_profile_prefs", Context.MODE_PRIVATE)

    fun save(profile: GazProfile) {
        prefs.edit()
            .putString(KEY_CAPACITY,  profile.capacityKg)
            .putString(KEY_BRAND,     profile.brand)
            .putString(KEY_LOCATION,  profile.usageLocation)
            .apply()
    }

    fun load(): GazProfile? {
        val capacity = prefs.getString(KEY_CAPACITY, null) ?: return null
        return GazProfile(
            capacityKg = capacity,
            brand  = prefs.getString(KEY_BRAND,    "") ?: "",
            usageLocation = prefs.getString(KEY_LOCATION, "") ?: "",
        )
    }

    fun clear() = prefs.edit().clear().apply()

    private companion object {
        const val KEY_CAPACITY = "capacity"
        const val KEY_BRAND    = "brand"
        const val KEY_LOCATION = "location"

    }
}