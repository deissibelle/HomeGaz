package cm.horion.homegaz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import cm.horion.homegaz.domain.usecase.RequestLocationPermissionUseCase
import cm.horion.homegaz.presentation.state.LocationPermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class LocationViewModel(
    private val requestLocationPermissionUseCase: RequestLocationPermissionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<LocationPermissionState>(LocationPermissionState.Idle)
    val state: StateFlow<LocationPermissionState> = _state.asStateFlow()


    fun onPermissionResult(isGranted: Boolean) {
        _state.value = when (requestLocationPermissionUseCase(isGranted)) {
            is RequestLocationPermissionUseCase.Result.Granted -> LocationPermissionState.Granted
            is RequestLocationPermissionUseCase.Result.Denied  -> LocationPermissionState.Denied
        }
    }
}
