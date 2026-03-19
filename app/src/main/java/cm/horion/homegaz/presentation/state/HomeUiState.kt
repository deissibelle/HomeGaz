package cm.horion.homegaz.presentation.state

import cm.horion.homegaz.domain.model.home.DistributorPoint
import com.google.android.gms.maps.model.LatLng

data class HomeUiState(
    val allPoints          : List<DistributorPoint> = emptyList(),
    val filteredPoints     : List<DistributorPoint> = emptyList(),
    val selectedPoint      : DistributorPoint?      = null,

    val selectedDistributor: String  = "Tous",
    val selectedDistance   : String  = "5 km",
    val selectedWeight     : String  = "Tous",

    val locationGranted    : Boolean = false,
    val userLat            : Double? = null,
    val userLng            : Double? = null,
    val userPhotoUrl       : String? = null,
    val routePolyline: List<LatLng> = emptyList(),

    val isLoading          : Boolean = false,
    val error              : String? = null
)