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
import com.yandex.mapkit.geometry.Point
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

    init {

        val alreadyGranted = hasLocationPermission()
        if (alreadyGranted) {
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
        _uiState.update { it.copy(selectedPoint = point) }
    }

    fun onDismissPopup() {
        _uiState.update { it.copy(selectedPoint = null, routePolyline = emptyList()) }
    }

    fun calculateRouteToPoint(destLat: Double, destLng: Double) {
        val startLat = _uiState.value.userLat
        val startLng = _uiState.value.userLng
        if (startLat == null || startLng == null) return
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(routePolyline = listOf(
                        Point(startLat, startLng),
                        Point(destLat, destLng)
                    ))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Impossible de charger l'itinéraire") }
            }
        }
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

    private fun applyFilters(points: List<DistributorPoint>, state: HomeUiState): List<DistributorPoint> {
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
}