package cm.horion.homegaz.domain.usecase


import cm.horion.homegaz.domain.model.distributor.DeliveryOption
import cm.horion.homegaz.domain.model.distributor.OrderSummary
import cm.horion.homegaz.domain.model.distributor.PaymentMethod

class BuildOrderSummaryUseCase {

    operator fun invoke(
        brand          : String,
        weight         : String,
        quantity       : Int,
        deliveryOption : DeliveryOption,
        paymentMethod  : PaymentMethod,
        phoneNumber    : String,
        unitPrice      : Int
    ): OrderSummary = OrderSummary(
        brand          = brand,
        weight         = weight,
        quantity       = quantity,
        deliveryOption = deliveryOption,
        paymentMethod  = paymentMethod,
        phoneNumber    = phoneNumber,
        unitPrice      = unitPrice
    )
}