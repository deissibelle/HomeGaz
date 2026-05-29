package cm.horion.homegaz.presentation.state

import cm.horion.homegaz.domain.model.distributor.DeliveryOption
import cm.horion.homegaz.domain.model.distributor.PaymentMethod


data class PaymentUiState(
    val brand          : String         = "",
    val weight         : String         = "",
    val quantity       : Int            = 1,
    val deliveryOption : DeliveryOption = DeliveryOption.LIVRAISON,
    val unitPrice      : Int            = 0,
    val selectedMethod : PaymentMethod  = PaymentMethod.OM,
    val phoneNumber    : String         = "",
    val isLoading      : Boolean        = false,
    val error          : String?        = null
) {
    val total       : Int     get() = unitPrice * quantity
    val isFormValid : Boolean get() = phoneNumber.isNotBlank()
}