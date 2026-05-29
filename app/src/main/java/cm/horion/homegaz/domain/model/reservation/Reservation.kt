package cm.horion.homegaz.domain.model.reservation

import cm.horion.homegaz.domain.model.distributor.DeliveryOption
import cm.horion.homegaz.domain.model.distributor.OrderSummary
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



enum class ReservationStatus {
    DELIVERING,
    PENDING,
    COMPLETED
}

data class Reservation(
    val id             : String,
    val brand          : String,
    val weight         : String,
    val quantity       : Int,
    val deliveryOption : String,
    val status         : ReservationStatus,
    val paymentMethod  : String,
    val price          : Int,
    val estimatedTime  : String? = null,
    val date           : String  = "",
    val time           : String? = null,
)

// Fabrique : OrderSummary → Reservation


private val DATE_FORMAT = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
private val TIME_FORMAT = SimpleDateFormat("HH:mm", Locale.getDefault())

/**
 * Convertit un [OrderSummary] confirmé en [Reservation] en état PENDING.
 *
 * Appelé dans [ReservationsViewModel.addReservationFromOrder] après que
 * l'utilisateur a validé son paiement sur [PaymentInitiatedScreen].
 */
fun OrderSummary.toReservation(): Reservation {
    val now = Date()

    val deliveryLabel = when (deliveryOption) {
        DeliveryOption.LIVRAISON -> "Livraison"
        DeliveryOption.RETRAIT   -> "Retrait"
    }

    val paymentLabel = when (paymentMethod) {
        PaymentMethod.OM -> "OM - $phoneNumber"
        PaymentMethod.MOMO         -> "MoMo - $phoneNumber"
    }

    val estimatedDelay = when (deliveryOption) {
        DeliveryOption.LIVRAISON -> "1H30"
        DeliveryOption.RETRAIT   -> "2 jours"
    }

    return Reservation(
        id             = "01 bt",
        brand          = brand,
        weight         = weight,
        quantity       = quantity,
        deliveryOption = deliveryLabel,
        status         = ReservationStatus.PENDING,
        paymentMethod  = paymentLabel,
        price          = total,
        estimatedTime  = estimatedDelay,
        date           = DATE_FORMAT.format(now),
        time           = TIME_FORMAT.format(now),
    )
}

val mockReservations: List<Reservation> = listOf(

    Reservation(
        id             = "01 bt",
        brand          = "Tradex",
        weight         = "12,5kg",
        quantity       = 1,
        deliveryOption = "Livraison",
        status         = ReservationStatus.DELIVERING,
        paymentMethod  = "OM - 698886644",
        price          = 7500,
        estimatedTime  = "1H30",
        date           = "10-02-2026",
        time           = "12:23"
    ),

    Reservation(
        id             = "02 bt",
        brand          = "Tradex",
        weight         = "12,5kg",
        quantity       = 1,
        deliveryOption = "Retrait",
        status         = ReservationStatus.COMPLETED,
        paymentMethod  = "OM - 698886644",
        price          = 6500,
        estimatedTime  = "2 jours",
        date           = "08-01-2026",
        time           = "08:56"
    ),

    Reservation(
        id             = "03 bt",
        brand          = "Tradex",
        weight         = "12,5kg",
        quantity       = 1,
        deliveryOption = "Retrait",
        status         = ReservationStatus.DELIVERING,
        paymentMethod  = "OM - 698886644",
        price          = 6500,
        estimatedTime  = "2 jours",
        date           = "08-12-2025",
        time           = "10:00"
    ),

    Reservation(
        id             = "04 bt",
        brand          = "Tradex",
        weight         = "12,5kg",
        quantity       = 1,
        deliveryOption = "Livraison",
        status         = ReservationStatus.PENDING,
        paymentMethod  = "OM - 698886644",
        price          = 6500,
        estimatedTime  = "2 jours",
        date           = "08-11-2025",
        time           = "09:15"
    ),

    Reservation(
        id             = "05 bt",
        brand          = "TotalEnergies",
        weight         = "6kg",
        quantity       = 2,
        deliveryOption = "Livraison",
        status         = ReservationStatus.PENDING,
        paymentMethod  = "MoMo - 677112233",
        price          = 9000,
        estimatedTime  = "45 min",
        date           = "15-03-2026",
        time           = "14:10"
    ),

    Reservation(
        id             = "06 bt",
        brand          = "Bocom",
        weight         = "12,5kg",
        quantity       = 1,
        deliveryOption = "Retrait",
        status         = ReservationStatus.COMPLETED,
        paymentMethod  = "OM - 699445566",
        price          = 7000,
        estimatedTime  = "1 jour",
        date           = "20-03-2026",
        time           = "16:45"
    ),

    Reservation(
        id             = "07 bt",
        brand          = "Green Oil",
        weight         = "50kg",
        quantity       = 1,
        deliveryOption = "Livraison",
        status         = ReservationStatus.DELIVERING,
        paymentMethod  = "MoMo - 680998877",
        price          = 32000,
        estimatedTime  = "2H00",
        date           = "22-03-2026",
        time           = "11:30"
    ),

    Reservation(
        id             = "08 bt",
        brand          = "Tradex",
        weight         = "6kg",
        quantity       = 3,
        deliveryOption = "Retrait",
        status         = ReservationStatus.PENDING,
        paymentMethod  = "OM - 655778899",
        price          = 13500,
        estimatedTime  = "3 jours",
        date           = "25-03-2026",
        time           = "09:05"
    ),

    Reservation(
        id             = "09 bt",
        brand          = "TotalEnergies",
        weight         = "12,5kg",
        quantity       = 2,
        deliveryOption = "Livraison",
        status         = ReservationStatus.COMPLETED,
        paymentMethod  = "MoMo - 670112244",
        price          = 15000,
        estimatedTime  = "1H15",
        date           = "28-03-2026",
        time           = "18:20"
    ),

    Reservation(
        id             = "10 bt",
        brand          = "Bocom",
        weight         = "50kg",
        quantity       = 1,
        deliveryOption = "Livraison",
        status         = ReservationStatus.PENDING,
        paymentMethod  = "OM - 690334455",
        price          = 30000,
        estimatedTime  = "3H00",
        date           = "30-03-2026",
        time           = "07:50"
    )
)