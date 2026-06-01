package cm.horion.homegaz.domain.model.payment.dto

import kotlinx.serialization.Serializable

@Serializable
data class SessionsResponse(
    val success : Boolean,
    val status : PaymentStatus = PaymentStatus.PENDING ,
    val message : String
)
