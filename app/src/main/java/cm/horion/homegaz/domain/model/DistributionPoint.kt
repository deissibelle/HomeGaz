package cm.horion.homegaz.domain.model

data class DistributionPoint(
    val id        : String,
    val name      : String,
    val imageUrl  : String = "",
    val latitude  : Double,
    val longitude : Double,
)