package cm.horion.homegaz.domain.model.order.dto

import kotlinx.serialization.Serializable


@Serializable
data class OrderRequest(
    val distributorUuid: String,
    val amount: Int,
    val deliveryMode : DeliveryMode = DeliveryMode.RETRAIT,
    val gaz: List<GazItem> = emptyList()
)
