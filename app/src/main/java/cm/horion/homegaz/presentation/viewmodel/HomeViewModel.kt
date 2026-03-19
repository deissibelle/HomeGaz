package cm.horion.homegaz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.horion.homegaz.domain.model.home.DistributorPoint
import cm.horion.homegaz.domain.usecase.GetDistributorPointsUseCase
import cm.horion.homegaz.presentation.state.HomeUiState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.*

class HomeViewModel(
    private val getDistributorPointsUseCase: GetDistributorPointsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadPoints() }

    private fun loadPoints() {
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

    fun calculateRoute(destination: LatLng) {
        val userLat = _uiState.value.userLat ?: return
        val userLng = _uiState.value.userLng ?: return
        val route = listOf(LatLng(userLat, userLng), destination)
        _uiState.update { it.copy(routePolyline = route) }
    }

    fun onLocationGranted(lat: Double?, lng: Double?, pointId: String? = null) {
        _uiState.update { state ->
            val updated  = state.copy(locationGranted = true, userLat = lat, userLng = lng)
            val filtered = applyFilters(state.allPoints, updated)
            val selected = pointId?.let { id -> filtered.find { it.id == id } }
            updated.copy(filteredPoints = filtered, selectedPoint = selected)
        }
    }

    fun onLocationDenied() {
        _uiState.update { it.copy(locationGranted = false, userLat = null, userLng = null, selectedPoint = null) }
    }

    fun onRecenter() {
        val lat = _uiState.value.userLat ?: return
        val lng = _uiState.value.userLng ?: return
        _uiState.update { it.copy(userLat = null, userLng = null) }
        _uiState.update { it.copy(userLat = lat,  userLng = lng)  }
    }

    fun onPointSelected(point: DistributorPoint) {
        _uiState.update { it.copy(selectedPoint = point) }
    }

    fun onPointDismissed() {
        _uiState.update { it.copy(selectedPoint = null, routePolyline = emptyList()) }
    }

    fun onDistributorChange(value: String) = updateFilter { copy(selectedDistributor = value) }
    fun onDistanceChange(value: String)    = updateFilter { copy(selectedDistance    = value) }
    fun onWeightChange(value: String)      = updateFilter { copy(selectedWeight      = value) }

    private fun updateFilter(update: HomeUiState.() -> HomeUiState) {
        _uiState.update { state ->
            val newState = state.update()
            newState.copy(filteredPoints = applyFilters(state.allPoints, newState))
        }
    }

    private fun applyFilters(points: List<DistributorPoint>, state : HomeUiState): List<DistributorPoint> {
        val lat = state.userLat ?: return points
        val lng = state.userLng ?: return points
        return points.filter { p ->
            (state.selectedDistributor == "Tous" || p.distributor.equals(state.selectedDistributor, true)) &&
                    (state.selectedWeight == "Tous" || p.weight.equals(state.selectedWeight, true))
        }.map { it.copy(distanceKm = haversineKm(lat, lng, it.latitude, it.longitude)) }
            .sortedBy { it.distanceKm }
    }

    private fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}