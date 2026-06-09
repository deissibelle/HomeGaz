package cm.horion.homegaz.domain.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cm.horion.homegaz.presentation.viewmodel.AppLanguage
import cm.horion.homegaz.presentation.viewmodel.AppTheme
import cm.horion.homegaz.presentation.viewmodel.DisplayPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.displayPrefsDataStore by preferencesDataStore(name = "display_prefs")


class DisplayPreferencesRepository(private val context: Context) {

    companion object {
        private val KEY_THEME    = stringPreferencesKey("theme")
        private val KEY_LANGUAGE = stringPreferencesKey("language")
    }

    val prefsFlow: Flow<DisplayPreferences> =
        context.displayPrefsDataStore.data.map { prefs ->
            DisplayPreferences(
                theme    = AppTheme.valueOf(
                    prefs[KEY_THEME] ?: AppTheme.SYSTEM.name
                ),
                language = AppLanguage.valueOf(
                    prefs[KEY_LANGUAGE] ?: AppLanguage.FRENCH.name
                )
            )
        }

    suspend fun setTheme(theme: AppTheme) {
        context.displayPrefsDataStore.edit { prefs ->
            prefs[KEY_THEME] = theme.name
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.displayPrefsDataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = language.name
        }
    }
}