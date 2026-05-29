package cm.horion.homegaz.domain.model.consommateur.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GazBottle(
    @SerialName(value = "_id")
    val id: String ,
    val uuid: String,
    val company: Company,
    val gazSize: GazSize, // ex: 12.5 kg
    val gazType: GazType
)
