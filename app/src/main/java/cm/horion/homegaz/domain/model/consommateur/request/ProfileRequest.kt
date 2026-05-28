package cm.horion.homegaz.domain.model.consommateur.request

import cm.horion.homegaz.domain.model.consommateur.dto.Address
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import kotlinx.serialization.Serializable

@Serializable
data class ProfileRequest(
    val address: Address,
    val paymentMethod: PaymentMethod,
    val gazBottle: String,
)