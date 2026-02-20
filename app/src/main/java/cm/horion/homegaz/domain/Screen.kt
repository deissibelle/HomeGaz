package cm.horion.homegaz.domain

sealed class Screen(val route: String) {
    data object Splash     : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Home       : Screen("home")
}