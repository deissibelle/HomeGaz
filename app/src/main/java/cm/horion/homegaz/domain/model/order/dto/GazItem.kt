package cm.horion.homegaz.domain.model.order.dto

import kotlinx.serialization.Serializable

@Serializable
data class GazItem(
    val bottleUuid: String,
    val quantity: Int
)
