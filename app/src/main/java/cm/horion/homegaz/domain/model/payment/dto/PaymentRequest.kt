package cm.horion.homegaz.domain.model.payment.dto

import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequest(
    val userUuid: String,
    val sessionsUuid: String,
    val serviceType: ServiceType,
    val amount: Double,
    val phoneNumber: String,
    val method: PaymentMethod
)
