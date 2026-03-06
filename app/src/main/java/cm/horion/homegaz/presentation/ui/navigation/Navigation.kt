package cm.horion.homegaz.presentation.ui.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cm.horion.homegaz.domain.model.common.Screen
import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.presentation.ui.MainScreen
import cm.horion.homegaz.presentation.ui.location.LocationPermissionScreen
import com.google.android.gms.location.LocationServices

@Composable
fun HomeGazApp(userPrefs: UserPreferencesRepository) {
    val navController = rememberNavController()
    val context = LocalContext.current

    var locationGranted by remember { mutableStateOf(false) }
    var returnedPointId by remember { mutableStateOf<String?>(null) }
    var returnedLat by remember { mutableStateOf<Double?>(null) }
    var returnedLng by remember { mutableStateOf<Double?>(null) }

    // Récupère la position GPS et retourne à Home
    fun fetchLocationAndGoHome(pId: String?) {
        LocationServices.getFusedLocationProviderClient(context)
            .lastLocation
            .addOnSuccessListener { loc ->
                returnedLat = loc?.latitude
                returnedLng = loc?.longitude
                returnedPointId = if (pId == "none") null else pId
                locationGranted = true
                navController.popBackStack()
            }
    }

    // Si permission déjà accordée → position directement, sinon → LocationPermissionScreen
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

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            MainScreen(
                locationGranted = locationGranted,
                pendingPointId = returnedPointId,
                userLat = returnedLat,
                userLng = returnedLng,
                // Click sur un point → LocationPermissionScreen avec l'id du point
                onMarkerClick = { id ->
                    navigateToPermissionOrFetch(id)
                },
                // Click sur actualiser → LocationPermissionScreen sans point spécifique
                onRefreshClick = {
                    navigateToPermissionOrFetch("none")
                }
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
    }
}