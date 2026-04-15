package cm.horion.homegaz.domain.model.reservation

enum class ReservationStatus(val label: String) {
    DELIVERING("EN LIVRAISON"),
    COMPLETED("TERMINÉ"),
    PENDING("EN ATTENTE")
}

data class Reservation(
    val id: String,
    val brand: String,
    val weight: String,
    val price: Int,
    val status: ReservationStatus,
    val paymentMethod: String = "Orange",
    val estimatedTime: String? = null
)

val mockReservations = listOf(
    // En cours de livraison
    Reservation(
        id = "HG-8829",
        brand = "SCTM",
        weight = "12.5 kg",
        price = 6500,
        status = ReservationStatus.DELIVERING,
        estimatedTime = "15 min"
    ),
    Reservation(
        id = "HG-9012",
        brand = "MRS",
        weight = "12.5 kg",
        price = 6500,
        status = ReservationStatus.DELIVERING,
        estimatedTime = "8 min"
    ),

    // Terminées
    Reservation(
        id = "HG-8740",
        brand = "Tradex",
        weight = "6 kg",
        price = 6500,
        status = ReservationStatus.COMPLETED
    ),
    Reservation(
        id = "HG-8620",
        brand = "Total",
        weight = "12.5 kg",
        price = 6500,
        status = ReservationStatus.COMPLETED
    ),
    Reservation(
        id = "HG-8510",
        brand = "SCTM",
        weight = "12.5 kg",
        price = 6500,
        status = ReservationStatus.COMPLETED
    ),

    // En attente
    Reservation(
        id = "HG-8830",
        brand = "SCTM",
        weight = "12.5 kg",
        price = 6500,
        status = ReservationStatus.PENDING,
        estimatedTime = "En attente"
    ),
    Reservation(
        id = "HG-9100",
        brand = "Tradex",
        weight = "12.5 kg",
        price = 6500,
        status = ReservationStatus.PENDING
    ),
    Reservation(
        id = "HG-9245",
        brand = "Aza",
        weight = "6 kg",
        price = 6500,
        status = ReservationStatus.PENDING
    ),

    Reservation(
        id = "HG-8100",
        brand = "Oilibya",
        weight = "12.5 kg",
        price = 6500,
        status = ReservationStatus.COMPLETED
    ),
    Reservation(
        id = "HG-7900",
        brand = "Tradex",
        weight = "12.5 kg",
        price = 6500,
        status = ReservationStatus.COMPLETED
    )
)
