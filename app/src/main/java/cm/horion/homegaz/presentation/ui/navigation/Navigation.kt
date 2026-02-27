package cm.horion.homegaz.presentation.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cm.horion.homegaz.domain.model.Screen
import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.presentation.ui.MainScreen
import cm.horion.homegaz.presentation.ui.location.LocationPermissionScreen
import cm.horion.homegaz.presentation.ui.onboarding.OnboardingScreen
import kotlinx.coroutines.launch

@Composable
fun HomeGazApp(userPrefs: UserPreferencesRepository) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val isOnboardingCompleted by userPrefs.isOnboardingCompleted.collectAsState(initial = null)

    if (isOnboardingCompleted == null) return

    NavHost(
        navController = navController,
        startDestination = if (isOnboardingCompleted == true) Screen.Home.route else Screen.Onboarding.route
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    scope.launch {
                        userPrefs.saveOnboardingCompleted(true)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                }
            )
        }

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