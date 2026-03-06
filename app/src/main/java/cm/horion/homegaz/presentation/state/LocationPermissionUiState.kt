package cm.horion.homegaz.presentation.state



sealed class LocationPermissionState {
    object Idle    : LocationPermissionState()
    object Granted : LocationPermissionState()
    object Denied  : LocationPermissionState()
}