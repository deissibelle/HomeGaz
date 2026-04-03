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
            .putString(KEY_CONSUMP,   profile.consumption)
            .putString(KEY_PHOTO,     profile.photoUri)
            .putString(KEY_NOTES,     profile.notes)
            .apply()
    }

    fun load(): GazProfile? {
        val capacity = prefs.getString(KEY_CAPACITY, null) ?: return null
        return GazProfile(
            capacityKg = capacity,
            brand  = prefs.getString(KEY_BRAND,    "") ?: "",
            usageLocation = prefs.getString(KEY_LOCATION, "") ?: "",
            consumption = prefs.getString(KEY_CONSUMP,  "") ?: "",
            photoUri  = prefs.getString(KEY_PHOTO,    null),
            notes = prefs.getString(KEY_NOTES,    null)
        )
    }

    fun clear() = prefs.edit().clear().apply()

    private companion object {
        const val KEY_CAPACITY = "capacity"
        const val KEY_BRAND    = "brand"
        const val KEY_LOCATION = "location"
        const val KEY_CONSUMP  = "consumption"
        const val KEY_PHOTO    = "photo_uri"
        const val KEY_NOTES    = "notes"
    }
}