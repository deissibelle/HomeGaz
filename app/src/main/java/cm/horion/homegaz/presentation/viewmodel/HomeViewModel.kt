package cm.horion.homegaz.presentation.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cm.horion.homegaz.domain.model.home.DistributorPoint
import cm.horion.homegaz.domain.usecase.GetDistributorPointsUseCase
import cm.horion.homegaz.presentation.state.HomeUiState
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.runtime.Error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.*

class HomeViewModel(
    application: Application,
    private val getDistributorPointsUseCase: GetDistributorPointsUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var drivingSession: DrivingSession? = null

    init {
        if (hasLocationPermission()) {
            _uiState.update { it.copy(locationGranted = true) }
        }
        loadPoints()
    }

    private fun hasLocationPermission(): Boolean {
        val ctx = getApplication<Application>()
        return ContextCompat.checkSelfPermission(
            ctx, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    ctx, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun loadPoints() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getDistributorPointsUseCase().collect { points ->
                _uiState.update { state ->
                    state.copy(
                        allPoints      = points,
                        filteredPoints = applyFilters(points, state),
                        isLoading      = false
                    )
                }
            }
        }
    }

    fun onLocationUpdated(lat: Double, lng: Double) {
        _uiState.update { state ->
            val newState = state.copy(userLat = lat, userLng = lng, locationGranted = true)
            newState.copy(filteredPoints = applyFilters(state.allPoints, newState))
        }
    }

    fun onLocationPermissionResult(isGranted: Boolean) {
        _uiState.update { it.copy(locationGranted = isGranted, locationDenied = !isGranted) }
    }

    fun onPointClick(point: DistributorPoint) {
        drivingSession?.cancel()
        drivingSession = null
        _uiState.update { it.copy(selectedPoint = point, routePolyline = emptyList(), routeBoundingBox = null) }
    }

    fun onDismissPopup() {
        drivingSession?.cancel()
        drivingSession = null
        _uiState.update { it.copy(selectedPoint = null, routePolyline = emptyList(), routeBoundingBox = null) }
    }

    // Appelé depuis HomeScreen : ferme d'abord la sheet, puis calcule la route
    fun calculateRouteToPoint(destLat: Double, destLng: Double) {
        val startLat = _uiState.value.userLat ?: return
        val startLng = _uiState.value.userLng ?: return

        drivingSession?.cancel()
        drivingSession = null

        // Ferme la sheet immédiatement pour laisser la carte visible
        _uiState.update { it.copy(
            selectedPoint    = null,
            isLoading        = true,
            error            = null,
            routePolyline    = emptyList(),
            routeBoundingBox = null
        )}

        val start = Point(startLat, startLng)
        val dest  = Point(destLat,  destLng)

        drivingSession = DirectionsFactory.getInstance()
            .createDrivingRouter(DrivingRouterType.COMBINED)
            .requestRoutes(
                listOf(
                    RequestPoint(start, RequestPointType.WAYPOINT, null, null,null),
                    RequestPoint(dest,  RequestPointType.WAYPOINT, null, null,null)
                ),
                DrivingOptions().apply { routesCount = 1 },
                VehicleOptions(),
                object : DrivingSession.DrivingRouteListener {

                    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
                        if (routes.isEmpty()) {
                            _uiState.update { it.copy(isLoading = false, error = "Aucun itinéraire trouvé") }
                            return
                        }
                        val geometry = routes[0].geometry.points
                        // Calcule le BoundingBox englobant tout l'itinéraire
                        // pour que la carte zoome automatiquement dessus
                        val bbox = computeBoundingBox(geometry)
                        _uiState.update { it.copy(
                            routePolyline    = geometry,
                            routeBoundingBox = bbox,
                            isLoading        = false
                        )}
                    }

                    override fun onDrivingRoutesError(error: Error) {
                        // Fallback ligne droite
                        val fallback = listOf(start, dest)
                        _uiState.update { it.copy(
                            routePolyline    = fallback,
                            routeBoundingBox = computeBoundingBox(fallback),
                            isLoading        = false,
                            error            = "Itinéraire approximatif (hors ligne)"
                        )}
                    }
                }
            )
    }

    // Calcule le rectangle englobant une liste de points GPS
    private fun computeBoundingBox(points: List<Point>): BoundingBox? {
        if (points.isEmpty()) return null
        val minLat = points.minOf { it.latitude }
        val maxLat = points.maxOf { it.latitude }
        val minLon = points.minOf { it.longitude }
        val maxLon = points.maxOf { it.longitude }
        return BoundingBox(
            Point(minLat, minLon), // sud-ouest
            Point(maxLat, maxLon)  // nord-est
        )
    }

    fun onDistributorChange(v: String) = updateFilter { copy(selectedDistributor = v) }
    fun onDistanceChange(v: String)    = updateFilter { copy(selectedDistance = v) }
    fun onWeightChange(v: String)      = updateFilter { copy(selectedWeight = v) }

    private fun updateFilter(update: HomeUiState.() -> HomeUiState) {
        _uiState.update { state ->
            val newState = state.update()
            newState.copy(filteredPoints = applyFilters(state.allPoints, newState))
        }
    }

    private fun applyFilters(
        points: List<DistributorPoint>,
        state : HomeUiState
    ): List<DistributorPoint> {
        return points
            .filter { p ->
                val mDist   = state.selectedDistributor == "Tous" ||
                        p.distributor.equals(state.selectedDistributor, true)
                val mWeight = state.selectedWeight == "Tous" ||
                        p.weight.contains(state.selectedWeight, true)
                mDist && mWeight
            }
            .map { p ->
                if (state.userLat != null && state.userLng != null)
                    p.copy(distanceKm = haversineKm(state.userLat, state.userLng, p.latitude, p.longitude))
                else p
            }
            .sortedBy { it.distanceKm }
    }

    private fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r    = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a    = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    override fun onCleared() {
        super.onCleared()
        drivingSession?.cancel()
    }
}