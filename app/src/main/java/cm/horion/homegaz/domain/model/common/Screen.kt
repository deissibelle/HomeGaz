package cm.horion.homegaz.domain.model.common



sealed class Screen(val route: String) {
    object Onboarding: Screen("onboarding")
    object Home: Screen("home")
    object LocationPermission: Screen("location_permission")
}