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

    init { loadPoints() }


    private fun loadPoints() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getDistributionPointsUseCase().collect { points ->
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


    fun onLocationGranted(lat: Double?, lng: Double?, pointId: String? = null) {
        _uiState.update { state ->
            val updated  = state.copy(locationGranted = true, userLat = lat, userLng = lng)
            val filtered = applyFilters(state.allPoints, updated)
            val selected = pointId?.let { id -> filtered.find { it.id == id } }
            updated.copy(filteredPoints = filtered, selectedPoint = selected)
        }
    }

    fun onLocationDenied() {
        _uiState.update { state ->
            state.copy(
                locationGranted = false,
                userLat         = null,
                userLng         = null,
                selectedPoint   = null,
                filteredPoints  = state.allPoints
            )
        }
    }

    fun onRecenter() {
        val lat = _uiState.value.userLat ?: return
        val lng = _uiState.value.userLng ?: return
        _uiState.update { it.copy(userLat = null, userLng = null) }
        _uiState.update { it.copy(userLat = lat,  userLng = lng)  }
    }


    fun onPointSelected(point: DistributionPoint) {
        _uiState.update { it.copy(selectedPoint = point) }
    }

    fun onPointDismissed() {
        _uiState.update { it.copy(selectedPoint = null) }
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

    fun setUserPhoto(url: String?) {
        _uiState.update { it.copy(userPhotoUrl = url) }
    }


    private fun applyFilters(
        points: List<DistributionPoint>,
        state : HomeUiState
    ): List<DistributionPoint> {
        val lat   = state.userLat
        val lng   = state.userLng
        val maxKm = state.selectedDistance
            .filter { it.isDigit() || it == '.' }
            .toDoubleOrNull() ?: 50.0

        return points
            .filter { p ->
                state.selectedDistributor == "Tous" ||
                        p.distributor.equals(state.selectedDistributor, ignoreCase = true)
            }
            .filter { p ->
                state.selectedWeight == "Tous" ||
                        p.weight.equals(state.selectedWeight, ignoreCase = true)
            }
            .let { filtered ->
                if (lat == null || lng == null) filtered
                else filtered
                    .map    { it.copy(distanceKm = haversineKm(lat, lng, it.latitude, it.longitude)) }
                    .filter { it.distanceKm <= maxKm }
                    .sortedBy { it.distanceKm }
            }
    }

    private fun haversineKm(
        lat1: Double, lng1: Double,
        lat2: Double, lng2: Double
    ): Double {
        val r    = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a    = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}