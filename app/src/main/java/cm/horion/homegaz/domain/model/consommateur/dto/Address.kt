package cm.horion.homegaz.domain.model.consommateur.dto

import cm.horion.homegaz.domain.model.consommateur.dto.AddressOwnerType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    @SerialName(value = "_id")
    val id: String = "",
    val userUuid: String? = null,
    val ownerType: AddressOwnerType = AddressOwnerType.PROFIL,
    val location: GeoLocation,
    val quarter: String,
    val city: String,
    val region: String,
    val country: String,
    val lieuDit: String
)

@Serializable
data class GeoLocation(
    val type: String = "Point",
    val coordinates: List<Double> // [longitude, latitude]
) {
    companion object {
        fun fromLatLng(latitude: Double, longitude: Double): GeoLocation {
            return GeoLocation(
                type = "Point",
                coordinates = listOf(longitude, latitude) // ✅ CORRECT
            )
        }
    }

    val longitude: Double get() = coordinates[0]
    val latitude: Double get() = coordinates[1]
}