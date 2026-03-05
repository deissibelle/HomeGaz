package cm.horion.homegaz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.horion.homegaz.domain.model.onboarding.Onboarding
import cm.horion.homegaz.domain.usecase.SaveOnboardingExitUseCase
import cm.horion.homegaz.presentation.state.OnboardingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val saveOnboardingExitUseCase: SaveOnboardingExitUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun setPages(pages: List<Onboarding>) {
        _uiState.update { it.copy(pages = pages) }
    }

    fun finishOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            saveOnboardingExitUseCase()
            onComplete()
        }
    }
}
