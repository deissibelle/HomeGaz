package cm.horion.homegaz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.horion.homegaz.domain.repository.DisplayPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

enum class AppLanguage {
    FRENCH, ENGLISH;

    fun code(): String = when (this) {
        FRENCH  -> "fr"
        ENGLISH -> "en"
    }
}

data class DisplayPreferences(
    val theme    : AppTheme    = AppTheme.SYSTEM,
    val language : AppLanguage = AppLanguage.FRENCH
)


class ThemeViewModel(
    private val repository: DisplayPreferencesRepository
) : ViewModel() {

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
