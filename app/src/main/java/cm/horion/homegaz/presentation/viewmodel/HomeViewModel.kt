package cm.horion.homegaz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.horion.homegaz.domain.model.home.DistributionPoint
import cm.horion.homegaz.domain.usecase.GetDistributionPointsUseCase
import cm.horion.homegaz.presentation.state.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.*

class HomeViewModel(
    private val getDistributionPointsUseCase: GetDistributionPointsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var userLat: Double? = null
    private var userLng: Double? = null

    init { loadPoints() }

    private fun loadPoints() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                getDistributionPointsUseCase().collect { points ->
                    _uiState.update {
                        it.copy(
                            allPoints = points,
                            filteredPoints = applyFilters(points, it),
                            isLoading = false
                        )
                    }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onLocationGranted(lat: Double?, lng: Double?, pointId: String? = null) {
        userLat = lat
        userLng = lng
        _uiState.update { it.copy(locationGranted = true) }
        applyFiltersWithLocation()
        if (pointId != null) {
            val point = _uiState.value.allPoints.firstOrNull { it.id == pointId }
            _uiState.update { it.copy(selectedPoint = point) }
        }
    }

    fun onLocationDenied() {
        _uiState.update { it.copy(locationGranted = false) }
    }
    fun onPointDismissed() {
        _uiState.update { it.copy(selectedPoint = null) }
    }

    fun onDistributorChange(value: String) {
        _uiState.update { state ->
            state.copy(
                selectedDistributor = value,
                filteredPoints = applyFilters(state.allPoints, state.copy(selectedDistributor = value))
            )
        }
    }

    fun onDistanceChange(value: String) {
        _uiState.update { state ->
            state.copy(
                selectedDistance = value,
                filteredPoints = applyFilters(state.allPoints, state.copy(selectedDistance = value))
            )
        }
    }

    fun onWeightChange(value: String) {
        _uiState.update { it.copy(selectedWeight = value) }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun applyFiltersWithLocation() {
        _uiState.update { state ->
            state.copy(filteredPoints = applyFilters(state.allPoints, state))
        }
    }

    private fun applyFilters(points: List<DistributionPoint>, state: HomeUiState): List<DistributionPoint> {
        val maxKm = state.selectedDistance.replace(" km", "").toDoubleOrNull() ?: 5.0
        val lat = userLat
        val lng = userLng

        return points
            .let { list ->
                if (state.selectedDistributor == "SCTM") list
                else list.filter { it.distributor == state.selectedDistributor }
            }
            .map { point ->
                if (lat != null && lng != null)
                    point.copy(distanceKm = haversineKm(lat, lng, point.latitude, point.longitude))
                else point
            }
            .let { list ->
                if (lat != null && lng != null) list.filter { it.distanceKm <= maxKm }
                else list
            }
            .sortedBy { it.distanceKm }
    }

    private fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        return R * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}