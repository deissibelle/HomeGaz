package cm.horion.homegaz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.horion.homegaz.domain.repository.DisplayPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Thème de l'application
 * – SYSTEM → suit le thème système Android
 * - LIGHT  → toujours clair
 * - DARK   → toujours sombre
 */
enum class AppTheme {
    SYSTEM, LIGHT, DARK;

    fun label(): String = when (this) {
        SYSTEM -> "Système"
        LIGHT  -> "Clair"
        DARK   -> "Sombre"
    }
}

// Langue de l'application
enum class AppLanguage {
    FRENCH, ENGLISH;

    fun label(): String = when (this) {
        FRENCH  -> "Français"
        ENGLISH -> "English"
    }

    fun code(): String = when (this) {
        FRENCH  -> "fr"
        ENGLISH -> "en"
    }
}

data class DisplayPreferences(
    val theme    : AppTheme    = AppTheme.SYSTEM,
    val language : AppLanguage = AppLanguage.FRENCH
)

/**
 * ViewModel singleton (via Koin) pour les préférences d'affichage.
 * Les préférences sont lues et écrites via DataStore → persistées entre sessions.
 *
 * Injecté à la racine de l'app (MainActivity / HomeGazTheme) pour que
 * le changement de thème soit appliqué globalement et immédiatement.
 */
class ThemeViewModel(
    private val repository: DisplayPreferencesRepository
) : ViewModel() {

    /**
     * StateFlow des préférences d'affichage.
     * WhileSubscribed(5000) : garde le flow actif 5s après la dernière
     * souscription (évite les recompositions inutiles lors des rotations).
     */
    val prefs: StateFlow<DisplayPreferences> = repository.prefsFlow
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = DisplayPreferences()
        )

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            repository.setTheme(theme)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            repository.setLanguage(language)
        }
    }
}