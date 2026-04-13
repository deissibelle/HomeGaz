package cm.horion.homegaz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.horion.homegaz.domain.model.reservation.mockReservations
import cm.horion.homegaz.presentation.state.ReservationsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReservationsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ReservationsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadReservations()
    }

    private fun loadReservations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulation de délai réseau
            kotlinx.coroutines.delay(1000)
            _uiState.update { it.copy(reservations = mockReservations, isLoading = false) }
        }
    }
}