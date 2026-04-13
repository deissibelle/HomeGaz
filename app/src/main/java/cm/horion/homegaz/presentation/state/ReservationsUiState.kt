package cm.horion.homegaz.presentation.state

import cm.horion.homegaz.domain.model.reservation.Reservation
import cm.horion.homegaz.domain.model.reservation.ReservationStatus

data class ReservationsUiState(
    val reservations: List<Reservation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val activeCount get() = reservations.count { it.status != ReservationStatus.COMPLETED && it.status != ReservationStatus.CANCELLED }
    val completedCount get() = reservations.count { it.status == ReservationStatus.COMPLETED }
}