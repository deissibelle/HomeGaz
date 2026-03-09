package cm.horion.homegaz.presentation.ui.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cm.horion.homegaz.domain.model.common.Screen
import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.presentation.ui.MainScreen
import cm.horion.homegaz.presentation.ui.distribution.DistributorPointDetailScreen
import cm.horion.homegaz.presentation.ui.location.LocationPermissionScreen
import cm.horion.homegaz.presentation.ui.onboarding.OnboardingScreen
import com.google.android.gms.location.LocationServices

@Composable
fun HomeGazApp(userPrefs: UserPreferencesRepository) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // État de l'onboarding pour la destination de départ
    val onboardingCompleted by userPrefs.isOnboardingCompleted.collectAsState(initial = null)

    var locationGranted by remember { mutableStateOf(false) }
    var returnedPointId by remember { mutableStateOf<String?>(null) }
    var returnedLat by remember { mutableStateOf<Double?>(null) }
    var returnedLng by remember { mutableStateOf<Double?>(null) }

    // Fonction utilitaire pour récupérer la position GPS
    fun fetchLocationAndGoHome(pId: String?) {
        LocationServices.getFusedLocationProviderClient(context)
            .lastLocation
            .addOnSuccessListener { loc ->
                returnedLat = loc?.latitude
                returnedLng = loc?.longitude
                returnedPointId = if (pId == "none") null else pId
                locationGranted = true
                navController.popBackStack(Screen.Home.route, false)
            }
    }

    // Gestion de la navigation vers permissions
    fun navigateToPermissionOrFetch(pointId: String) {
        val alreadyGranted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (alreadyGranted) {
            fetchLocationAndGoHome(pointId)
        } else {
            navController.navigate("${Screen.LocationPermission.route}/$pointId")
        }
    }

    if (onboardingCompleted == null) return

    NavHost(
        navController = navController,
        startDestination = if (onboardingCompleted == true) Screen.Home.route else Screen.Onboarding.route
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

        composable(Screen.Home.route) {
            MainScreen(
                navController = navController,
                locationGranted = locationGranted,
                pendingPointId = returnedPointId,
                userLat = returnedLat,
                userLng = returnedLng,
                onMarkerClick = { id -> navigateToPermissionOrFetch(id) },
                onRefreshClick = { navigateToPermissionOrFetch("none") }
            )
        }

        composable("${Screen.LocationPermission.route}/{pointId}") { backStackEntry ->
            val pId = backStackEntry.arguments?.getString("pointId")

            LocationPermissionScreen(
                onPermissionGranted = {
                    val confirmed = ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (confirmed) {
                        fetchLocationAndGoHome(pId)
                    } else {
                        navController.popBackStack()
                    }
                },
                onPermissionDenied = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "distributor_detail/{pointId}",
            arguments = listOf(navArgument("pointId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pointId = backStackEntry.arguments?.getString("pointId")

            DistributorPointDetailScreen(
                onBackClick = { navController.popBackStack() },
                onNextClick = { quantity, option ->
                }
            )
        }
    }
}
