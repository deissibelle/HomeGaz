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
        val granted = hasLocationPermission()
        _uiState.update { it.copy(locationGranted = granted, isFirstLaunch = !granted) }
        // Charge les points immédiatement avec le centre par défaut (Yaoundé)
        // Dès que la position GPS est disponible, loadPoints() est rappelé
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


    // Utilise la position réelle si disponible, sinon centre par défaut
    fun loadPoints() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val state = _uiState.value

            // Rayon en km extrait du filtre distance
            val radiusKm = parseDistanceKm(state.selectedDistance)

            // Centre : position réelle ou Yaoundé par défaut
            val centerLat = state.userLat ?: 3.848
            val centerLng = state.userLng ?: 11.502

            getDistributorPointsUseCase(
                centerLat = centerLat,
                centerLng = centerLng,
                radiusKm  = radiusKm
            ).collect { points ->
                _uiState.update { s ->
                    s.copy(
                        allPoints      = points,
                        filteredPoints = applyFilters(points, s),
                        isLoading      = false
                    )
                }
            }
        }
    }


    fun onLocationUpdated(lat: Double, lng: Double) {
        val wasWithoutLocation = _uiState.value.userLat == null
        _uiState.update { state ->
            val newState = state.copy(
                userLat        = lat,
                userLng        = lng,
                locationGranted = true,
                isFirstLaunch  = false
            )
            newState.copy(filteredPoints = applyFilters(state.allPoints, newState))
        }
        if (wasWithoutLocation) loadPoints()
    }

    fun onLocationPermissionResult(isGranted: Boolean) {
        _uiState.update {
            it.copy(
                locationGranted = isGranted,
                locationDenied  = !isGranted,
                isFirstLaunch   = false
            )
        }
        // Si accordée, recharge les points
        if (isGranted) loadPoints()
    }

    // Sélection d'un point
    fun onPointClick(point: DistributorPoint) {
        drivingSession?.cancel()
        drivingSession = null
        _uiState.update {
            it.copy(
                selectedPoint    = point,
                routePolyline    = emptyList(),
                routeBoundingBox = null
            )
        }
    }

    fun onDismissPopup() {
        drivingSession?.cancel()
        drivingSession = null
        _uiState.update {
            it.copy(
                selectedPoint    = null,
                routePolyline    = emptyList(),
                routeBoundingBox = null
            )
        }
    }

    // Calcul d'itinéraire
    fun calculateRouteToPoint(destLat: Double, destLng: Double) {
        val startLat = _uiState.value.userLat ?: return
        val startLng = _uiState.value.userLng ?: return

        drivingSession?.cancel()
        drivingSession = null

        _uiState.update {
            it.copy(
                selectedPoint    = null,
                isLoading        = true,
                error            = null,
                routePolyline    = emptyList(),
                routeBoundingBox = null
            )
        }

        val start = Point(startLat, startLng)
        val dest  = Point(destLat,  destLng)

        drivingSession = DirectionsFactory.getInstance()
            .createDrivingRouter(DrivingRouterType.COMBINED)
            .requestRoutes(
                listOf(
                    RequestPoint(start, RequestPointType.WAYPOINT, null, null, null),
                    RequestPoint(dest,  RequestPointType.WAYPOINT, null, null, null)
                ),
                DrivingOptions().apply { routesCount = 1 },
                VehicleOptions(),
                object : DrivingSession.DrivingRouteListener {
                    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
                        if (routes.isEmpty()) {
                            _uiState.update {
                                it.copy(isLoading = false, error = "Aucun itinéraire trouvé")
                            }
                            return
                        }
                        val geometry = routes[0].geometry.points
                        val bbox     = computeBoundingBox(geometry)
                        _uiState.update {
                            it.copy(
                                routePolyline    = geometry,
                                routeBoundingBox = bbox,
                                isLoading        = false
                            )
                        }
                    }

                    override fun onDrivingRoutesError(error: Error) {
                        val fallback = listOf(start, dest)
                        _uiState.update {
                            it.copy(
                                routePolyline    = fallback,
                                routeBoundingBox = computeBoundingBox(fallback),
                                isLoading        = false,
                                error            = "Itinéraire approximatif (hors ligne)"
                            )
                        }
                    }
                }
            )
    }

    //Filtres
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
        val maxDistKm = parseDistanceKm(state.selectedDistance)

        return points
            .map { p ->
                // Calcule la distance réelle si la position est connue
                if (state.userLat != null && state.userLng != null)
                    p.copy(distanceKm = haversineKm(state.userLat, state.userLng, p.latitude, p.longitude))
                else p
            }
            .filter { p ->
                val matchDistributor = state.selectedDistributor == "Tous" ||
                        p.distributor.equals(state.selectedDistributor, ignoreCase = true)

                val matchWeight = state.selectedWeight == "Tous" ||
                        p.weight.contains(state.selectedWeight, ignoreCase = true)

                // Filtre distance uniquement si la position est connue
                val matchDistance = state.userLat == null ||
                        p.distanceKm <= maxDistKm

                matchDistributor && matchWeight && matchDistance
            }
            .sortedBy { it.distanceKm }
    }

    //  Utilitaires

    /** "5 km" → 5.0 | "100 mètre" → 0.1 | défaut → 5.0 */
    private fun parseDistanceKm(raw: String): Double {
        val lower = raw.lowercase().trim()
        return when {
            lower.contains("mètre") || lower.contains("metre") || lower.contains("m") && !lower.contains("km") -> {
                val metres = lower.filter { it.isDigit() }.toDoubleOrNull() ?: 500.0
                metres / 1000.0
            }
            else -> lower.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 5.0
        }
    }

    private fun computeBoundingBox(points: List<Point>): BoundingBox? {
        if (points.isEmpty()) return null
        val minLat = points.minOf { it.latitude }
        val maxLat = points.maxOf { it.latitude }
        val minLon = points.minOf { it.longitude }
        val maxLon = points.maxOf { it.longitude }
        return BoundingBox(Point(minLat, minLon), Point(maxLat, maxLon))
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