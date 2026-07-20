package cm.horion.homegaz.domain.model.order.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    @SerialName(value = "_id")
    val id: String,
    val uuid: String,
    val userUuid: String,
    val distributorUuid: String,
    val amount: Int,
    val orderState: OrderState = OrderState.LOADING,
    val deliveryMode : DeliveryMode = DeliveryMode.RETRAIT,
    val referenceTransaction: String? = null,
    val deliveryCode: String,
    val gaz:  List<GazItem> = emptyList(),
    val createdAt: String,
    val updatedAt: String
)
