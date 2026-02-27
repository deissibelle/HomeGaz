package cm.horion.homegaz.domain.model


data class DistributionPoint(
    val id: String,
    val name: String,
    val imageUrl: String = "",
    val latitude: Double,
    val longitude: Double,
    val distributor: String = "SCTM",
    val price: String = "6500 FCFA",
    val distance: String = "0.0 km"
)