package cm.horion.homegaz.domain.model.common



sealed class Screen(val route: String) {
    object Onboarding: Screen("onboarding")
    object Home: Screen("home")
    object LocationPermission: Screen("location_permission")

    object DistributorDetail : Screen("distributor_detail/{pointId}") {
        fun createRoute(pointId: String) = "distributor_detail/${pointId}"
    }
    object Payment: Screen("payment")
    object Confirmation: Screen("confirmation")
    object PaymentInitiated   : Screen("payment_initiated")
    object PaymentSuccess     : Screen("payment_success")
    object GazProfile         : Screen("gaz_profile")
    object HelpCenter : Screen("help_center_screen")
    object PrivacySettings : Screen("privacy_settings_screen")


}