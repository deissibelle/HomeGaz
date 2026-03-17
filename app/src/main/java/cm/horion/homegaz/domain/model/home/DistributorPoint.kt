package cm.horion.homegaz.domain.model.home

data class DistributorPoint(
    val id: String,
    val name: String,
    val imageUrl: String = "",
    val latitude: Double,
    val longitude: Double,
    val distributor: String = "SCTM",
    val weight: String = "12.5kg",
    val logoRes        : Int?   = null,
    val priceXaf: Int = 6500,
    val distanceKm: Double = 0.0,
    val stockAvailable: Boolean = true
)