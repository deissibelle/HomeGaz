package cm.horion.homegaz.domain.model.home

data class DistributionPoint(
    val id: String,
    val name: String,
    val imageUrl: String = "",
    val latitude: Double,
    val longitude: Double,
    val distributor: String = "SCTM",
    val priceXaf: Int = 6500,
    val distanceKm: Double = 0.0
)