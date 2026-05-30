package cm.horion.homegaz.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cm.horion.homegaz.domain.model.Endpoint
import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle
import cm.horion.homegaz.util.ApiClient.client
import cm.horion.homegaz.util.Constants.GAZ_URL
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json

class GazBottleLocal(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val KEY_GAZ_BOTTLES = stringPreferencesKey("key_gaz_bottles")
    }

    suspend fun saveGazBottles(bottles: List<GazBottle>) {
        val jsonString = Json.encodeToString(bottles)
        dataStore.edit { preferences -> preferences[KEY_GAZ_BOTTLES] = jsonString }
    }

    suspend fun getGazBottles(): List<GazBottle>? {
        val preferences = dataStore.data.firstOrNull()
        val jsonString = preferences?.get(KEY_GAZ_BOTTLES)
        return if (!jsonString.isNullOrEmpty()) {
            runCatching { Json.decodeFromString<List<GazBottle>>(jsonString) }.getOrNull()
        } else null
    }

    suspend fun getGazBottleByUuid(uuid: String): GazBottle? {
        return getGazBottles()?.find { it.uuid == uuid }
    }

}