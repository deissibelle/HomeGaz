package cm.horion.homegaz.presentation.state

import cm.horion.homegaz.domain.model.consommateur.dto.Company
import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle
import cm.horion.homegaz.domain.model.consommateur.dto.GazSize
import cm.horion.homegaz.domain.model.distributor.dto.Distributor
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point

data class ConsumerUiState(
    val allPoints  : List<Distributor> = emptyList(),
    val filteredPoints: List<Distributor> = emptyList(),
    val selectedPoint  : Distributor?      = null,

    // Filtres
    val selectedDistributor: String = "Tous",
    val selectedDistance: String = "5 km",
    val selectedWeight: String = "Tous",
    val battleUuid: String = "",
    val availableBottles: List<GazBottle> = emptyList(),


    // Localisation & Permissions
    val userLat: Double? = null,
    val userLng: Double? = null,
    val locationGranted: Boolean = false,
    val locationDenied: Boolean = false,
    val isFirstLaunch: Boolean = true,
    val isLocationFetched: Boolean = false,
    val isRefiningLocation: Boolean = false,

    // Itinéraire Yandex
    val routePolyline: List<Point> = emptyList(),
    val routeBoundingBox: BoundingBox? = null,

    val isLoading: Boolean = false,
    val error    : String? = null,
)
