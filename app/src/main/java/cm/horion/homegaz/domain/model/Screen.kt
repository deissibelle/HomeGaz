package cm.horion.homegaz.domain.model

sealed class Screen(val route: String) {
    data object Splash     : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Home       : Screen("home")
    object LocationPermission  : Screen("location_permission")
}