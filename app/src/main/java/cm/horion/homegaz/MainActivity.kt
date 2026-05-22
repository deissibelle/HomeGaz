package cm.horion.homegaz

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.presentation.ui.navigation.HomeGazApp
import cm.horion.homegaz.presentation.ui.theme.HomeGazTheme
import cm.horion.homegaz.presentation.viewmodel.HomeViewModel
import com.google.android.gms.location.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val userPrefs   : UserPreferencesRepository by inject()
    private val homeViewModel: HomeViewModel             by viewModel()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback   : LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    homeViewModel.onLocationUpdated(location.latitude, location.longitude)
                }
            }
        }

        setContent {
            HomeGazTheme {
                HomeGazApp(userPrefs = userPrefs)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000L)
            .setMinUpdateDistanceMeters(10f)
            .build()
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onResume() {
        super.onResume()
        if (homeViewModel.uiState.value.locationGranted) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}