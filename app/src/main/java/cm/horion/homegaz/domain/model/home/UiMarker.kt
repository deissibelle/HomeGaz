package cm.horion.homegaz.domain.model.home


data class UiMarker(
    val id: String,
    val title: String,
    val snippet: String,
    val latitude: Double,
    val longitude: Double
)

fun DistributionPoint.toUiMarker() = UiMarker(
    id = id,
    title = name,
    snippet   = distributor,
    latitude  = latitude,
    longitude = longitude
)