package com.homegaz.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.homegaz.app.domain.model.AuthTokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// DataStore extension
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "homegaz_preferences"
)

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {

    private val dataStore = context.dataStore

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val TOKEN_EXPIRES_IN = longPreferencesKey("token_expires_in")
        private val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val LAST_LOCATION_LAT = doublePreferencesKey("last_location_lat")
        private val LAST_LOCATION_LNG = doublePreferencesKey("last_location_lng")
    }

    // -------------------------
    // Auth Tokens
    // -------------------------

    suspend fun saveAuthTokens(tokens: AuthTokens) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = tokens.accessToken
            preferences[REFRESH_TOKEN] = tokens.refreshToken
            preferences[TOKEN_EXPIRES_IN] = tokens.expiresIn
        }
    }

    suspend fun getAuthTokens(): AuthTokens? {
        val preferences = dataStore.data.first()

        val accessToken = preferences[ACCESS_TOKEN]
        val refreshToken = preferences[REFRESH_TOKEN]
        val expiresIn = preferences[TOKEN_EXPIRES_IN]

        return if (accessToken != null && refreshToken != null && expiresIn != null) {
            AuthTokens(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = expiresIn
            )
        } else {
            null
        }
    }

    suspend fun clearAuthTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
            preferences.remove(TOKEN_EXPIRES_IN)
            preferences.remove(CURRENT_USER_ID)
        }
    }

    // -------------------------
    // Current User
    // -------------------------

    suspend fun saveCurrentUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[CURRENT_USER_ID] = userId
        }
    }

    suspend fun getCurrentUserId(): String? {
        return dataStore.data
            .map { it[CURRENT_USER_ID] }
            .first()
    }

    // -------------------------
    // Onboarding
    // -------------------------

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    fun isOnboardingCompleted(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }
    }

    // -------------------------
    // Last Location
    // -------------------------

    suspend fun saveLastLocation(latitude: Double, longitude: Double) {
        dataStore.edit { preferences ->
            preferences[LAST_LOCATION_LAT] = latitude
            preferences[LAST_LOCATION_LNG] = longitude
        }
    }

    suspend fun getLastLocation(): Pair<Double, Double>? {
        val preferences = dataStore.data.first()

        val lat = preferences[LAST_LOCATION_LAT]
        val lng = preferences[LAST_LOCATION_LNG]

        return if (lat != null && lng != null) {
            Pair(lat, lng)
        } else {
            null
        }
    }
}
