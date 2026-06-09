package cm.horion.homegaz.domain.model.consommateur.dto

import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Profile (
    @SerialName(value = "_id")
    val id: String ,
    val userUuid: String,
    val address: Address,
    val paymentMethod: PaymentMethod,
    val gazBottle: String,
    val createdAt: String,
    val updatedAt: String
)
