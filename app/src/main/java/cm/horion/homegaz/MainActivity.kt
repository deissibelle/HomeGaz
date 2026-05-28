package cm.horion.homegaz

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.presentation.ui.navigation.HomeGazApp
import cm.horion.homegaz.presentation.ui.theme.HomeGazTheme
import cm.horion.homegaz.presentation.viewmodel.HomeViewModel
import com.google.android.gms.location.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val userPrefs : UserPreferencesRepository by inject()
    private val homeViewModel: HomeViewModel by viewModel()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback   : LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    homeViewModel.onLocationUpdated(loc.latitude, loc.longitude)
                }
            }
        }

        lifecycleScope.launch {
            homeViewModel.uiState.collect { state ->
                if (state.locationGranted) startLocationUpdates()
            }
        }

        setContent {
            HomeGazTheme {
                var showSplash by remember { mutableStateOf(true) }
                if (showSplash) {
                    HomeGazSplashScreen(onFinished = { showSplash = false })
                } else {
                    HomeGazApp(userPrefs = userPrefs)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        if (!::fusedLocationClient.isInitialized) return
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L)
            .setMinUpdateDistanceMeters(10f)
            .build()
        fusedLocationClient.requestLocationUpdates(
            request, locationCallback, Looper.getMainLooper()
        )
    }

    override fun onResume() {
        super.onResume()
        if (homeViewModel.uiState.value.locationGranted) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        if (::fusedLocationClient.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}


@Composable
private fun HomeGazSplashScreen(onFinished: () -> Unit) {

    val isDark     = isSystemInDarkTheme()
    val background = if (isDark) Color(0xFF0D1B2A) else Color.White

    val scale      = remember { Animatable(0.7f) }
    val alpha      = remember { Animatable(0f) }
    val orionAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue   = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness    = Spring.StiffnessMediumLow
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue   = 1f,
                animationSpec = tween(durationMillis = 500, easing = EaseOut)
            )
        }
        delay(600L)
        orionAlpha.animateTo(
            targetValue   = 1f,
            animationSpec = tween(durationMillis = 400, easing = EaseOut)
        )
        delay(1_400L)
        launch {
            alpha.animateTo(
                targetValue   = 0f,
                animationSpec = tween(durationMillis = 350, easing = EaseIn)
            )
        }
        orionAlpha.animateTo(
            targetValue   = 0f,
            animationSpec = tween(durationMillis = 350, easing = EaseIn)
        )
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .systemBarsPadding()
    ) {
        Image(
            painter            = painterResource(id = R.drawable.logo_splash),
            contentDescription = "Logo HomeGaz",
            modifier           = Modifier
                .align(Alignment.Center)
                .size(110.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        )
        Image(
            painter            = painterResource(
                id = if (isDark) R.drawable.by_orion_white else R.drawable.by_orion
            ),
            contentDescription = "by Orion",
            modifier           = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 52.dp)
                .height(18.dp)
                .alpha(orionAlpha.value)
        )
    }
}