package cm.horion.homegaz.domain.model.distributor

data class OrderSummary(
    val brand          : String,
    val weight         : String,
    val quantity       : Int,
    val deliveryOption : DeliveryOption,
    val paymentMethod  : PaymentMethod,
    val phoneNumber    : String,
    val unitPrice      : Int,
    val currency       : String = "frs"
) {
    val total: Int get() = unitPrice * quantity
}