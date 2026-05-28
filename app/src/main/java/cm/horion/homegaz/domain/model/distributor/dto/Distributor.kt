package cm.horion.homegaz.domain.model.distributor.dto

import cm.horion.homegaz.domain.model.consommateur.dto.Address
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Distributor(
    @SerialName(value = "_id")
    val id: String,
    val enterpriseUuid: String? = null,
    val name: String,
    val managerContact : String,
    val storeContact   : String,
    val address: Address,
    val openTime : String,
    val closeTime : String,
    val accountState: AccountState,
    val stock: Map<String, Int> = mapOf(),
    val distance : Double,
    val createdAt: String,
    val updatedAt: String
)
