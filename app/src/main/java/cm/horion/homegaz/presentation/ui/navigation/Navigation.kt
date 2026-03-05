package cm.horion.homegaz.presentation.ui.navigation

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cm.horion.homegaz.domain.model.common.Screen
import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.presentation.ui.MainScreen
import cm.horion.homegaz.presentation.ui.location.LocationPermissionScreen
import cm.horion.homegaz.presentation.ui.onboarding.OnboardingScreen
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

private const val POINT_ID_ARG  = "pointId"
private const val POINT_ID_NONE = "none"

@SuppressLint("MissingPermission")
@Composable
fun HomeGazApp(userPrefs: UserPreferencesRepository) {
    val navController = rememberNavController()
    val scope         = rememberCoroutineScope()
    val context       = LocalContext.current

    val isOnboardingCompleted by userPrefs.isOnboardingCompleted.collectAsState(initial = null)
    if (isOnboardingCompleted == null) return

    // État partagé retourné vers HomeScreen après accord de permission
    var locationGranted by remember { mutableStateOf(false) }
    var locationDenied  by remember { mutableStateOf(false) }
    var returnedPointId by remember { mutableStateOf<String?>(null) }
    var returnedLat     by remember { mutableStateOf<Double?>(null) }
    var returnedLng     by remember { mutableStateOf<Double?>(null) }

    NavHost(
        navController = navController,
        startDestination = if (isOnboardingCompleted == true) Screen.Home.route
        else Screen.Onboarding.route
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
                onMarkerClick  = { pointId ->
                    locationGranted = false
                    locationDenied  = false
                    navController.navigate("${Screen.LocationPermission.route}/$pointId")
                },
                onRefreshClick = {
                    locationGranted = false
                    locationDenied  = false
                    navController.navigate("${Screen.LocationPermission.route}/$POINT_ID_NONE")
                },
                pendingPointId  = returnedPointId,
                userLat         = returnedLat,
                userLng         = returnedLng,
                locationGranted = locationGranted,
                locationDenied  = locationDenied
            )
        }

        composable(
            route     = "${Screen.LocationPermission.route}/{$POINT_ID_ARG}",
            arguments = listOf(navArgument(POINT_ID_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val pointId = backStackEntry.arguments
                ?.getString(POINT_ID_ARG)
                .takeIf { it != POINT_ID_NONE }

            LocationPermissionScreen(
                onPermissionGranted = {
                    LocationServices
                        .getFusedLocationProviderClient(context)
                        .lastLocation
                        .addOnSuccessListener { loc: Location? ->
                            returnedPointId = pointId
                            returnedLat     = loc?.latitude
                            returnedLng     = loc?.longitude
                            locationGranted = true
                            locationDenied  = false
                            navController.popBackStack()
                        }
                        .addOnFailureListener {
                            returnedPointId = pointId
                            returnedLat     = null
                            returnedLng     = null
                            locationGranted = true
                            locationDenied  = false
                            navController.popBackStack()
                        }
                },
                onPermissionDenied = {
                    locationDenied  = true
                    locationGranted = false
                    navController.popBackStack()
                }
            )
        }
    }
}