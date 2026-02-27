package cm.horion.homegaz.domain.model

data class UiMarker(
    val id: String,
    val lat: Double,
    val lng: Double,
    val title: String = ""
)