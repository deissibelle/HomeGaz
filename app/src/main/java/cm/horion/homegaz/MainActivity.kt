package cm.horion.homegaz

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.presentation.ui.navigation.HomeGazApp
import cm.horion.homegaz.presentation.ui.theme.HomeGazTheme
import cm.horion.homegaz.presentation.viewmodel.ConsumerViewModel
import cm.horion.homegaz.util.LocationUtils
import com.google.android.gms.location.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MainActivity : ComponentActivity() {

    private val userPrefs : UserPreferencesRepository by inject()
    private val homeViewModel: ConsumerViewModel by viewModel()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback   : LocationCallback

    // Déclarer le lanceur de permission
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Permission accordée, on vérifie maintenant si le GPS est activé
                checkGpsSettings()
            }
            else -> {
                // Permission refusée : Gérer le cas
            }
        }
    }

    private var isTrackingLocation = false

    override fun onCreate(savedInstanceState: Bundle?) {

        //installSplashScreen()
        val splashScreen = installSplashScreen()


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ✅ La Splash Screen attend de manière déterministe le stockage ET la position géographique
        splashScreen.setKeepOnScreenCondition {
            !homeViewModel.isDataReady || (homeViewModel.uiState.value.locationGranted && !homeViewModel.isLocationFetched)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    // Met à jour l'UI en continu si l'utilisateur se déplace
                    homeViewModel.onLocationChanged(loc.latitude, loc.longitude)
                }
            }
        }

        // Démarre l'écouteur de flux uniquement pour suivre les changements à chaud après l'ouverture
        lifecycleScope.launch {
            homeViewModel.uiState.collect { state ->
                // On ne démarre les mises à jour que si la permission est acquise ET qu'on ne l'a pas déjà fait
                if (state.locationGranted && !isTrackingLocation) {
                    startLocationUpdates()
                }
            }
        }

        setContent {
            RequestNotificationPermissionHandler()
            HomeGazTheme {
                HomeGazApp(userPrefs = userPrefs)
            }
        }

        checkAndRequestLocation()
    }

    private fun checkAndRequestLocation() {
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION

        // On vérifie si l'une des deux permissions est déjà accordée
        val isFineGranted = ContextCompat.checkSelfPermission(this, fineLocationPermission) == PackageManager.PERMISSION_GRANTED
        val isCoarseGranted = ContextCompat.checkSelfPermission(this, coarseLocationPermission) == PackageManager.PERMISSION_GRANTED

        if (isFineGranted || isCoarseGranted) {
            // ✅ Permissions déjà accordées : on vérifie juste si le bouton GPS est allumé
            checkGpsSettings()
        } else {
            // ❌ Permissions manquantes : on lance la demande système
            locationPermissionRequest.launch(arrayOf(fineLocationPermission, coarseLocationPermission))
        }
    }


    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        if (!::fusedLocationClient.isInitialized || isTrackingLocation) return

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L)
            .setMinUpdateDistanceMeters(10f)
            .build()

        fusedLocationClient.requestLocationUpdates(
            request, locationCallback, Looper.getMainLooper()
        )
        isTrackingLocation = true // ✅ On marque que l'écouteur est branché
    }

    override fun onResume() {
        super.onResume()
        if (homeViewModel.uiState.value.locationGranted && !isTrackingLocation) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::fusedLocationClient.isInitialized && isTrackingLocation) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            isTrackingLocation = false
        }
    }

    private fun checkGpsSettings() {
        if (!LocationUtils.isLocationEnabled(this)) {
            // Le GPS est désactivé dans les réglages système
            AlertDialog.Builder(this)
                .setTitle("GPS désactivé")
                .setMessage("Pour fonctionner correctement, l'application a besoin du GPS. Voulez-vous l'activer ?")
                .setPositiveButton("Oui") { _, _ ->
                    LocationUtils.showLocationSettings(this)
                }
                .setNegativeButton("Non", null)
                .show()
        }
    }

    @Composable
    fun RequestNotificationPermissionHandler() {
        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Log.d("PAYEMENT", "Permission de notification accordée par l'utilisateur !")
            } else {
                Log.d("PAYEMENT", "Permission de notification refusée.")
            }
        }

        LaunchedEffect(Unit) {
            // La permission POST_NOTIFICATIONS n'existe et n'est requise qu'à partir d'Android 13 (API 33)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

}

