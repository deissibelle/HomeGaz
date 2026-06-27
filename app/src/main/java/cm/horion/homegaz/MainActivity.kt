package cm.horion.homegaz

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import cm.horion.homegaz.data.security.AuthState
import cm.horion.homegaz.data.security.UserDataStore
import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.presentation.ui.navigation.HomeGazApp
import cm.horion.homegaz.presentation.ui.theme.HomeGazTheme
import cm.horion.homegaz.presentation.viewmodel.ConsumerViewModel
import cm.horion.homegaz.presentation.viewmodel.HomeViewModel
import cm.horion.homegaz.util.LocationUtils
import cm.horion.homegaz.util.isExpiredSoon
import com.google.android.gms.location.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MainActivity : ComponentActivity() {

    private val userPrefs : UserPreferencesRepository by inject()
    private val homeViewModel: ConsumerViewModel by viewModel()
    private val userSettings: UserDataStore by inject()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback   : LocationCallback
    private lateinit var ssoClient: OrionSsoClient

    private var isAuthStoreReady = false

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
            checkGpsSettings()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {

        //installSplashScreen()
        val splashScreen = installSplashScreen()

        // Ferme le splash immédiatement sans attendre
        //splashScreen.setKeepOnScreenCondition { false }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            !homeViewModel.isDataReady
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    homeViewModel.onLocationChanged(
                        loc.latitude,
                        loc.longitude,
                        loc.accuracy   // ✅ passe la précision GPS
                    )
                }
            }
        }

        lifecycleScope.launch {
            // 1. On charge d'abord le token actuellement stocké localement
            userSettings.onAppStart()

            // 2. On vérifie s'il a besoin d'être rafraîchi
            val needsLogin = checkAndRefreshIfNeeded()

            if (needsLogin) {
                ssoClient.startExchange()
            }

            // 3. Tout est prêt, on libère le Splash Screen
            isAuthStoreReady = true

            // Écoute de la géoloc
            homeViewModel.uiState.collect { state ->
                if (state.locationGranted) startLocationUpdates()
            }
        }

        ssoClient = OrionSsoClient(
            activity = this,
            serviceName = "GAZ",
            onLoginSuccess = { exchangeToken ->
                println("HomeGaz SSO Success: Nouveau jeton reçu -> $exchangeToken")
                lifecycleScope.launch {
                    userSettings.onLoginSuccess(exchangeToken)
                }
            },
            onLogoutSuccess = {
                lifecycleScope.launch { userSettings.logout() }
            },
            onErrorOrCancel = { message ->
                println("SSO HomeGaz Info/Erreur: $message")
            }
        )

        setContent {
            val token by userSettings.tokenFlow.collectAsStateWithLifecycle()
            val isLoggedIn = if(token != null && token?.isExpiredSoon() == false){
                true
            } else {
                false
            }

            HomeGazTheme {
                HomeGazApp(
                    userPrefs = userPrefs,
                    isLoggedIn = isLoggedIn,
                    onSsoLoginCall = { ssoClient.launchAuth() },
                    onSsoLogoutCall = { ssoClient.startLogout() }
                )
            }
        }

        checkAndRequestLocation()
    }


    private suspend fun checkAndRefreshIfNeeded(): Boolean {
        val currentToken = userSettings.getExchangeToken() ?: return false

        val expired = currentToken.isExpiredSoon() ?: true

        return if (expired) {
            println("Le token local est expiré ou va expirer, demande de rafraîchissement...")
            true // On signale qu'un rafraîchissement via le SSO est requis
        } else {
            println("Le token local est encore parfaitement valide.")
            false // Rien à faire
        }
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
            painter            = painterResource(id = if(isDark) R.drawable.logoblanc  else R.drawable.logo_splash),
            contentDescription = "Logo HomeGaz",
            modifier           = Modifier
                .align(Alignment.Center)
                .size(110.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        )
        Image(
            painter  = painterResource(
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