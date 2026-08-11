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
    val deliveryOption : DeliveryOption,
    val status         : ReservationStatus,
    val paymentMethod  : String,
    val price          : Int,
    val date           : String  = "",
    val time           : String? = null,
)

private val DATE_FORMAT = SimpleDateFormat("dd-MM-yyyy", Locale.US)
private val TIME_FORMAT = SimpleDateFormat("HH:mm", Locale.US)

/**
 * Convertit un [OrderSummary] confirmé en [Reservation] propre pour le domaine.
 */
fun OrderSummary.toReservation(): Reservation {
    val now = Date()

    val paymentLabel = when (paymentMethod) {
        PaymentMethod.OM   -> "OM - $phoneNumber"
        PaymentMethod.MOMO -> "MoMo - $phoneNumber"
    }

    return Reservation(
        id             = "01 bt",
        brand          = brand,
        weight         = weight,
        quantity       = quantity,
        deliveryOption = deliveryOption, // On passe directement l'enum
        status         = ReservationStatus.PENDING,
        paymentMethod  = paymentLabel,
        price          = total,
        date           = DATE_FORMAT.format(now),
        time           = TIME_FORMAT.format(now),
    )
}

// Mock mis à jour avec les structures Enum propres
val mockReservations: List<Reservation> = listOf(
    Reservation(
        id             = "01 bt",
        brand          = "Tradex",
        weight         = "12,5kg",
        quantity       = 1,
        deliveryOption = DeliveryOption.LIVRAISON,
        status         = ReservationStatus.DELIVERING,
        paymentMethod  = "OM - 698886644",
        price          = 7500,
        date           = "10-02-2026",
        time           = "12:23"
    ),
    Reservation(
        id             = "02 bt",
        brand          = "Tradex",
        weight         = "12,5kg",
        quantity       = 1,
        deliveryOption = DeliveryOption.RETRAIT,
        status         = ReservationStatus.COMPLETED,
        paymentMethod  = "OM - 698886644",
        price          = 6500,
        date           = "08-01-2026",
        time           = "08:56"
    )
)
