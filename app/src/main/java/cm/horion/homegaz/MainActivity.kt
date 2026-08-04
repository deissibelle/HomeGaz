package cm.horion.homegaz

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import cm.horion.homegaz.data.security.AuthState
import cm.horion.homegaz.data.security.UserDataStore
import cm.horion.homegaz.domain.repository.AuthRepository
import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.presentation.ui.components.account.SsoLoadingDialog
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
    private val authRepository : AuthRepository by inject()
    private val homeViewModel: ConsumerViewModel by viewModel()
    private val userSettings: UserDataStore by inject()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback   : LocationCallback
    private lateinit var ssoClient: OrionSsoClient

    private var isAuthStoreReady = false
    private var isLoading by mutableStateOf(false)
    private var isAuthCheckCompleted by mutableStateOf(false)

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
        splashScreen.setKeepOnScreenCondition {
            !isAuthCheckCompleted || !homeViewModel.isDataReady
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
            //isLoading = true
            // 1. On charge d'abord le token actuellement stocké localement
            userSettings.onAppStart()

            // 2. On écoute le verdict réel
            userSettings.authState.collect { state ->
                if (state != AuthState.Checking) {
                    // 🎯 Dès qu'on est Authenticated ou Unauthenticated, on valide la fin de la vérification
                    isAuthCheckCompleted = true
                    isLoading = false
                }
            }


            // Écoute de la géoloc
            homeViewModel.uiState.collect { state ->
                if (state.locationGranted) startLocationUpdates()
            }
        }

        handleAuthRedirect(intent)

        setContent {
            val authState by userSettings.authState.collectAsStateWithLifecycle()

            // 🎯 Les pros gèrent explicitement l'état "En cours de vérification" au niveau de l'UI globale
            val isChecking = authState is AuthState.Checking
            val isLoggedIn = authState is AuthState.Authenticated

            HomeGazTheme {
                if (isLoading) {
                    SsoLoadingDialog()
                }
                HomeGazApp(
                    userPrefs = userPrefs,
                    isLoggedIn = isLoggedIn,
                    onSsoLoginCall = { launchCustomTabsLogin() },
                    onSsoLogoutCall = {
                        lifecycleScope.launch { userSettings.logout() }
                    }
                )
            }
        }

        checkAndRequestLocation()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Intercepter si le navigateur nous renvoie le code alors que l'app était en arrière-plan
        handleAuthRedirect(intent)
    }


    private fun launchCustomTabsLogin() {
        val mobileCallback = "orion-homegaz://callback"
        val encodedCallback = Uri.encode(mobileCallback)
        val authUrl = "https://auth.horion.io/?redirect=$encodedCallback"

        val customTabsIntent = androidx.browser.customtabs.CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()

        // 🎯 Forcer l'intention à s'ouvrir uniquement dans un navigateur Web
        val intent = customTabsIntent.intent
        intent.data = Uri.parse(authUrl)

        // On cherche s'il y a un navigateur compatible (ex: Chrome) pour casser l'interception automatique des autres apps
        val packageNavigator = androidx.browser.customtabs.CustomTabsClient.getPackageName(this, listOf("com.android.chrome"))
        if (packageNavigator != null) {
            intent.setPackage(packageNavigator)
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback si aucun package Custom Tabs n'est résolu
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
                startActivity(fallbackIntent)
            } catch (ex: Exception) {
                android.util.Log.e("AUTH", "Aucun navigateur disponible sur l'appareil : ${ex.message}")
            }
        }
    }


    private fun handleAuthRedirect(intent: Intent?) {
        val data = intent?.data
        // 💡 Correction : Doit écouter "orion-homegaz" mis dans launchCustomTabsLogin !
        if (data != null && data.scheme == "orion-homegaz" && data.host == "callback") {
            val code = data.getQueryParameter("code")
            val item = data.getQueryParameter("item")

            if (!code.isNullOrBlank() && !item.isNullOrBlank()) {
                lifecycleScope.launch {
                    try {
                        isLoading = true // 🎯 On active le SsoLoadingDialog pendant l'échange réseau
                        val response = authRepository.getToken(code, item)

                        if (response.success) {
                            isLoading = false
                        } else {
                            android.util.Log.e("AUTH", "L'échange de jeton a échoué côté API")
                            isLoading = false
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AUTH", "Erreur réseau lors de la récupération du token : ${e.message}")
                    } finally {
                        isLoading = false // 🎯 On désactive le chargement dans tous les cas
                    }
                }
            }
        }
    }


//    private suspend fun checkAndRefreshIfNeeded(): Boolean {
//        val currentToken = userSettings.getExchangeToken() ?: return false
//
//        val expired = currentToken.isExpiredSoon() ?: true
//
//        return if (expired) {
//            println("Le token local est expiré ou va expirer, demande de rafraîchissement...")
//            true // On signale qu'un rafraîchissement via le SSO est requis
//        } else {
//            println("Le token local est encore parfaitement valide.")
//            false // Rien à faire
//        }
//    }

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