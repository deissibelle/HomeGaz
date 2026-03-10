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
            _uiState.update { it.copy(isLoading = true) }
            getDistributionPointsUseCase().collect { points ->
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

    fun onLocationGranted(lat: Double?, lng: Double?, pointId: String? = null) {
        userLat = lat
        userLng = lng
        _uiState.update { state ->
            val newState = state.copy(locationGranted = true)
            val filtered = applyFilters(state.allPoints, newState)
            val selected = if (pointId != null) filtered.find { it.id == pointId } else null
            newState.copy(filteredPoints = filtered, selectedPoint = selected)
        }
    }

    fun onLocationDenied() {
        userLat = null
        userLng = null
        _uiState.update { state ->
            state.copy(
                locationGranted = false,
                selectedPoint = null,
                filteredPoints = state.allPoints
            )
        }
    }

    fun onPointSelected(point: DistributionPoint) {
        if (_uiState.value.locationGranted) {
            _uiState.update { it.copy(selectedPoint = point) }
        }
    }

    fun onPointDismissed() {
        _uiState.update { it.copy(selectedPoint = null) }
    }

    fun onDistributorChange(value: String) {
        _uiState.update { state ->
            val newState = state.copy(selectedDistributor = value)
            newState.copy(filteredPoints = applyFilters(state.allPoints, newState))
        }
    }

    fun onDistanceChange(value: String) {
        _uiState.update { state ->
            val newState = state.copy(selectedDistance = value)
            newState.copy(filteredPoints = applyFilters(state.allPoints, newState))
        }
    }
    fun onWeightChange(value: String) {
        _uiState.update { state ->
            state.copy(selectedWeight = value)
        }
    }

    private fun applyFilters(points: List<DistributionPoint>, state: HomeUiState): List<DistributionPoint> {
        if (userLat == null || userLng == null) return points

        val maxKm = state.selectedDistance.filter { it.isDigit() }.toDoubleOrNull() ?: 50.0

        return points
            .filter { it.distributor == state.selectedDistributor || state.selectedDistributor == "SCTM" }
            .map { it.copy(distanceKm = haversineKm(userLat!!, userLng!!, it.latitude, it.longitude)) }
            .filter { it.distanceKm <= maxKm }
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
