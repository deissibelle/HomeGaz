package cm.horion.homegaz.domain.model.reservation


enum class ReservationStatus(val label: String) {
    PENDING("En attente"),
    CONFIRMED("Confirmé"),
    DELIVERING("En livraison"),
    COMPLETED("Terminé"),
    CANCELLED("Annulé")
}

data class Reservation(
    val id: String,
    val brand: String,
    val weight: String,
    val price: Int,
    val status: ReservationStatus,
    val date: String,
    val deliveryAddress: String? = null,
    val pickupCode: String? = null,
    val estimatedTime: String? = null // Ex: "15 min"
)




val mockReservations = listOf(
    Reservation("HG-8829", "SCTM", "12.5 kg", 6500, ReservationStatus.DELIVERING, "13 Avr", estimatedTime = "15 min"),
    Reservation("HG-8740", "Tradex", "6 kg", 3500, ReservationStatus.COMPLETED, "10 Avr"),
    Reservation("HG-9012", "Total", "12.5 kg", 6500, ReservationStatus.PENDING, "13 Avr")
)