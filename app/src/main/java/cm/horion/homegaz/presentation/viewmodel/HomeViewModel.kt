package cm.horion.homegaz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.horion.homegaz.domain.model.home.DistributorPoint
import cm.horion.homegaz.domain.usecase.GetDistributorPointsUseCase
import cm.horion.homegaz.presentation.state.HomeUiState
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

    fun loadPoints() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getDistributorPointsUseCase().collect { points ->
                _uiState.update { state ->
                    state.copy(
                        allPoints = points,
                        filteredPoints = applyFilters(points, state),
                        isLoading = false
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
        _uiState.update { it.copy(selectedPoint = null) }
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
        return points.filter { p ->
            val mDist = state.selectedDistributor == "Tous" || p.distributor.equals(state.selectedDistributor, true)
            val mWeight = state.selectedWeight == "Tous" || p.weight.contains(state.selectedWeight, true)
            mDist && mWeight
        }.map { p ->
            if (state.userLat != null && state.userLng != null) {
                p.copy(distanceKm = haversineKm(state.userLat, state.userLng, p.latitude, p.longitude))
            } else p
        }.sortedBy { it.distanceKm }
    }

    private fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}