package cm.horion.homegaz.domain.model.order.dto


import kotlinx.serialization.Serializable

@Serializable
data class OrderQrCodeData(
    val uuid: String,
    val quantity: Int,
    val bottleUuid: String,
    val amount: Int
)

