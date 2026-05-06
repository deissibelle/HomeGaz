package cm.horion.homegaz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.horion.homegaz.domain.model.distributor.OrderSummary
import cm.horion.homegaz.domain.model.reservation.mockReservations
import cm.horion.homegaz.domain.model.reservation.toReservation
import cm.horion.homegaz.presentation.state.ReservationsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel des réservations — instance unique partagée dans tout le graphe
 * de navigation grâce au scope Koin (singleton ou activity-scoped).
 *
 * Responsabilités :
 *  - Charger la liste des réservations (mock → plus tard : repository)
 *  - Exposer l'état UI via [StateFlow] (pattern UDF)
 *  - Recevoir un [OrderSummary] confirmé et le convertir en [Reservation]
 */
class ReservationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationsUiState())
    val uiState: StateFlow<ReservationsUiState> = _uiState.asStateFlow()

    init {
        loadReservations()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Chargement initial
    // ─────────────────────────────────────────────────────────────────────────

    private fun loadReservations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Simulation délai réseau — remplacer par repository.getReservations()
                kotlinx.coroutines.delay(1_000)
                _uiState.update {
                    it.copy(reservations = mockReservations, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Erreur inconnue")
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Ajout d'une réservation depuis le tunnel de commande
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Convertit le [OrderSummary] confirmé en [Reservation] (via extension fun)
     * et l'insère en tête de liste.
     *
     * Appelé dans [Navigation.kt] → `onDone` de [PaymentInitiatedScreen],
     * avant la navigation vers [PaymentSuccessScreen].
     */
    fun addReservationFromOrder(summary: OrderSummary) {
        val newReservation = summary.toReservation()
        _uiState.update { current ->
            current.copy(
                reservations = listOf(newReservation) + current.reservations
            )
        }
    }
}