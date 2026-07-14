package cm.horion.homegaz.presentation.state

import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle
import cm.horion.homegaz.domain.model.order.dto.Order
import cm.horion.homegaz.domain.model.reservation.Reservation
import cm.horion.homegaz.domain.model.reservation.ReservationStatus


data class ReservationsUiState(
    val reservations : List<Reservation> = emptyList(),
    val orders : List<Order> = emptyList(),
    val gaz              : GazBottle?     = null,
    val availableBottles : List<GazBottle>? = emptyList(),
    val isLoading    : Boolean           = false,
    val error        : String?           = null,
) {

    val activeCount: Int
        get() = reservations.count {
            it.status == ReservationStatus.DELIVERING ||
                    it.status == ReservationStatus.PENDING
        }

    val completedCount: Int
        get() = reservations.count { it.status == ReservationStatus.COMPLETED }
}