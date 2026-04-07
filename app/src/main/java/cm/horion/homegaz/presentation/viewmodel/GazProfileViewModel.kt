package cm.horion.homegaz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.horion.homegaz.domain.model.gazprofile.GazProfile
import cm.horion.homegaz.domain.usecase.LoadGazProfileUseCase
import cm.horion.homegaz.domain.usecase.SaveGazProfileUseCase
import cm.horion.homegaz.presentation.state.GazProfileUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GazProfileViewModel(
    private val saveProfile : SaveGazProfileUseCase,
    private val loadProfile : LoadGazProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GazProfileUiState())
    val uiState: StateFlow<GazProfileUiState> = _uiState.asStateFlow()

    init {
        loadExistingProfile()
    }


    private fun loadExistingProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = loadProfile() ?: return@launch
            _uiState.update {
                it.copy(
                    capacityKg    = profile.capacityKg,
                    brand         = profile.brand,
                    usageLocation = profile.usageLocation,

                )
            }
        }
    }

    fun onCapacityChange(value: String)  = _uiState.update { it.copy(capacityKg    = value) }
    fun onBrandChange(value: String)     = _uiState.update { it.copy(brand         = value) }
    fun onLocationChange(value: String)  = _uiState.update { it.copy(usageLocation = value) }


    fun saveProfile(onSuccess: () -> Unit = {}) {
        val state = _uiState.value
        if (!state.isFormValid) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                saveProfile(
                    GazProfile(
                        capacityKg = state.capacityKg,
                        brand = state.brand,
                        usageLocation = state.usageLocation,

                    )
                )
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
                launch(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = "Erreur lors de la sauvegarde")
                }
            }
        }
    }
}