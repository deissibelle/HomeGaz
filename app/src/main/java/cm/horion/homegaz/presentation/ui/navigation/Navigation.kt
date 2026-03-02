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
import cm.horion.homegaz.presentation.viewmodel.OnboardingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeGazApp(userPrefs: UserPreferencesRepository) {
    val navController = rememberNavController()
    
    // On observe l'état du DataStore pour la destination initiale
    val isOnboardingCompleted by userPrefs.isOnboardingCompleted.collectAsState(initial = null)

    // On attend que la valeur soit chargée pour éviter un écran blanc ou un saut d'écran
    if (isOnboardingCompleted == null) return

    NavHost(
        navController = navController,
        startDestination = if (isOnboardingCompleted == true) Screen.Home.route else Screen.Onboarding.route
    ) {
        // --- ONBOARDING ---
        composable(Screen.Onboarding.route) {
            val onboardingViewModel: OnboardingViewModel = koinViewModel()
            
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onFinish = {
                    // On demande au ViewModel de sauvegarder, puis on navigue
                    onboardingViewModel.finishOnboarding(
                        onComplete = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }
            )
        }

        // --- HOME ---
        composable(Screen.Home.route) {
            MainScreen(
                onMarkerClick = {
                    navController.navigate(Screen.LocationPermission.route)
                }
            )
        }

        // --- PERMISSION ---
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
