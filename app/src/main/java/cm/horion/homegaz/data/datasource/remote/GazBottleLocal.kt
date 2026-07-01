package cm.horion.homegaz.data.datasource.remote

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json

class GazBottleLocal(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val KEY_GAZ_BOTTLES = stringPreferencesKey("key_gaz_bottles")
    }

    suspend fun saveGazBottles(bottles: List<GazBottle>) {
        val jsonString = Json.Default.encodeToString(bottles)
        dataStore.edit { preferences -> preferences[KEY_GAZ_BOTTLES] = jsonString }
    }

    suspend fun getGazBottles(): List<GazBottle>? {
        val preferences = dataStore.data.firstOrNull()
        val jsonString = preferences?.get(KEY_GAZ_BOTTLES)
        return if (!jsonString.isNullOrEmpty()) {
            runCatching { Json.Default.decodeFromString<List<GazBottle>>(jsonString) }.getOrNull()
        } else null
    }

    suspend fun getGazBottleByUuid(uuid: String): GazBottle? {
        return getGazBottles()?.find { it.uuid == uuid }
    }

}