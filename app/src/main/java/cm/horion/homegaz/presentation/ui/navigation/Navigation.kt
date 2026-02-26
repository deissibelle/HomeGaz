package cm.horion.homegaz.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cm.horion.homegaz.domain.model.Screen
import cm.horion.homegaz.presentation.ui.MainScreen
import cm.horion.homegaz.presentation.ui.location.LocationPermissionScreen
import cm.horion.homegaz.presentation.ui.onboarding.OnboardingScreen

@Composable
fun HomeGazApp() {
    val navController = rememberNavController()

    NavHost(
        navController    = navController,
        startDestination = Screen.Onboarding.route
    ) {

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // MainScreen = HomeScreen + BottomNavBar + toutes les tabs
        composable(Screen.Home.route) {
            MainScreen(
                onMarkerClick = {
                    navController.navigate(Screen.LocationPermission.route)
                }
            )
        }

        composable(Screen.LocationPermission.route) {
            LocationPermissionScreen(
                onPermissionGranted = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.LocationPermission.route) { inclusive = true }
                    }
                },
                onPermissionDenied = {
                    navController.popBackStack()
                }
            )
        }
    }
}