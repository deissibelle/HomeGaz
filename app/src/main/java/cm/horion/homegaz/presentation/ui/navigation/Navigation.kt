package cm.horion.homegaz.presentation.ui.navigation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cm.horion.homegaz.domain.model.common.Screen
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.domain.model.payment.dto.PaymentStatus
import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.presentation.ui.MainScreen
import cm.horion.homegaz.presentation.ui.Tab
import cm.horion.homegaz.presentation.ui.pages.account.HelpCenterScreen
import cm.horion.homegaz.presentation.ui.pages.account.PrivacySettingsScreen
import cm.horion.homegaz.presentation.ui.pages.confirmation.ConfirmationScreen
import cm.horion.homegaz.presentation.ui.pages.distributor.DistributorPointDetailScreen
import cm.horion.homegaz.presentation.ui.pages.gazprofile.GazProfileScreen
import cm.horion.homegaz.presentation.ui.pages.location.LocationPermissionScreen
import cm.horion.homegaz.presentation.ui.pages.onboarding.OnboardingScreen
import cm.horion.homegaz.presentation.ui.pages.payment.PaymentInitiatedScreen
import cm.horion.homegaz.presentation.ui.pages.payment.PaymentScreen
import cm.horion.homegaz.presentation.ui.pages.payment.PaymentSuccessScreen
import cm.horion.homegaz.presentation.ui.theme.HomeGazTheme
import cm.horion.homegaz.presentation.viewmodel.ConsumerViewModel
import cm.horion.homegaz.presentation.viewmodel.DistributorDetailViewModel
import cm.horion.homegaz.presentation.viewmodel.HomeViewModel
import cm.horion.homegaz.presentation.viewmodel.ReservationsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeGazApp(
    userPrefs: UserPreferencesRepository,
    isLoggedIn            : Boolean,
    onSsoLoginCall        : () -> Unit,
    onSsoLogoutCall       : () -> Unit
) = HomeGazTheme {

    val navController = rememberNavController()

    val onboardingCompleted by userPrefs
        .isOnboardingCompleted
        .collectAsState(initial = null)

    if (onboardingCompleted == null) return@HomeGazTheme

    val startDestination = if (onboardingCompleted == true) {
        Screen.Home.route
    } else {
        Screen.Onboarding.route
    }

    val homeViewModel: HomeViewModel               = koinViewModel()
    val consumerViewModel: ConsumerViewModel = koinViewModel()
    val distributorViewModel  : DistributorDetailViewModel = koinViewModel()
    val reservationsViewModel: ReservationsViewModel = koinViewModel()
    val sharedUiState by distributorViewModel.uiState.collectAsState()



    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {

        // ── ONBOARDING ────────────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ── LOCATION PERMISSION ───────────────────────────────────────────────
        composable(Screen.LocationPermission.route) {

            val locationPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val granted = permissions.values.all { it }
                homeViewModel.onLocationPermissionResult(granted)
                if (granted) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.LocationPermission.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            LocationPermissionScreen(
                onActivateClick = {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            )
        }

        // ── HOME ──────────────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            MainScreen(
                navController         = navController,
                reservationsViewModel = reservationsViewModel,
                consumerViewModel = consumerViewModel,
                onRouteClick          = { lat, lng ->
                    homeViewModel.calculateRouteToPoint(lat, lng)
                },
                onRequestLocation = {
                    navController.navigate(Screen.LocationPermission.route) {
                        launchSingleTop = true
                    }
                },
                isLoggedIn = isLoggedIn,
                onSsoLoginCall = onSsoLoginCall,
                onSsoLogoutCall = onSsoLogoutCall
            )
        }
        //  Centre d'aide
        composable(Screen.HelpCenter.route) {
            HelpCenterScreen(
                onBackClick = {
                    navController.navigate("${Screen.Home.route}/${Tab.ACCOUNT.label}") {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },                onLicencesClick = {  }
            )
        }

        //  Confidentialité des données
        composable(Screen.PrivacySettings.route) {
            PrivacySettingsScreen(
                onBackClick = {
                    navController.navigate("${Screen.Home.route}/${Tab.ACCOUNT.label}") {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },            )
        }

        // ── HOME WITH TAB ─────────────────────────────────────────────────────
        composable(
            route     = "${Screen.Home.route}/{initialTab}",
            arguments = listOf(navArgument("initialTab") { type = NavType.StringType })
        ) { backStackEntry ->
            val initialTab = backStackEntry.arguments?.getString("initialTab") ?: Tab.HOME.label
            MainScreen(
                navController         = navController,
                reservationsViewModel = reservationsViewModel,
                consumerViewModel = consumerViewModel,
                initialTab            = initialTab,
                onRouteClick          = { lat, lng ->
                    homeViewModel.calculateRouteToPoint(lat, lng)
                },
                onRequestLocation = {
                    navController.navigate(Screen.LocationPermission.route) {
                        launchSingleTop = true
                    }
                },
                isLoggedIn = isLoggedIn,
                onSsoLoginCall = onSsoLoginCall,
                onSsoLogoutCall = onSsoLogoutCall
            )
        }

        // ── DISTRIBUTOR DETAIL ────────────────────────────────────────────────
        composable(
            route           = Screen.DistributorDetail.route,
            arguments       = listOf(navArgument("pointId") { type = NavType.StringType }),
            enterTransition = { fadeIn(tween(400)) + scaleIn(initialScale = 0.92f) }
        ) { backStackEntry ->

            // 2. 🚀 COLLECTE L'ÉTAT ICI EN DESSOUS : Compose va maintenant observer les changements réels !
            val uiState by consumerViewModel.uiState.collectAsState()

            val pointId = backStackEntry.arguments?.getString("pointId") ?: ""

            // 3. On cherche le point dans l'état fraîchement collecté
            val point = uiState.allPoints.find { it.enterpriseUuid == pointId }
            if (point != null) {
                DistributorPointDetailScreen(
                    point       = point,
                    battleUuid  = uiState.battleUuid,
                    viewModel   = distributorViewModel,
                    onBackClick = { navController.navigate(Screen.Home.route)
                    },
                    onNextClick = { quantity, deliveryOption ->
                        navController.navigate(Screen.Payment.route)
                    }
                )
            } else {
                // Optionnel : Affiche un écran de chargement ou d'erreur temporaire au lieu du noir total
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        // ── PAYMENT ───────────────────────────────────────────────────────────
        composable(Screen.Payment.route) {
            PaymentScreen(
                uiState          = sharedUiState,
                onPhoneChange    = { distributorViewModel.onPhoneNumberChange(it) },
                onMethodSelected = { distributorViewModel.onPaymentMethodChange(it) },
                onBackClick    = { navController.popBackStack() },
                onNextClick    = {
                    navController.navigate(Screen.Confirmation.route)
                }
            )
        }

        // ── CONFIRMATION ──────────────────────────────────────────────────────
        composable(Screen.Confirmation.route) {
                ConfirmationScreen(
                    uiState        = sharedUiState,
                    onBackClick    = { navController.popBackStack() },
                    onStartOrder   = distributorViewModel::initOrder,
                    onStartPayment = distributorViewModel::initPayment,
                    dismissError   = distributorViewModel::dismissError,
                    viewModel   = distributorViewModel,
                    onModifyClick  = {
                        navController.popBackStack(Screen.DistributorDetail.route, false)
                    },
                    onConfirmClick = {
                        distributorViewModel.cleanPayment()
                        navController.navigate(Screen.PaymentInitiated.route)
                    }
                )
        }

        // ── PAYMENT INITIATED ─────────────────────────────────────────────────
        composable(Screen.PaymentInitiated.route) {
            PaymentInitiatedScreen(
                uiState       = sharedUiState,
                paymentMethod = sharedUiState.selectedMethod ?: PaymentMethod.OM,
                onDone        = {
                    // Le worker a fini en SUCCEEDED -> Direction l'écran de succès
                    navController.navigate(Screen.PaymentSuccess.route) {
                        // On efface l'écran d'attente de la pile pour éviter un retour arrière dessus
                        popUpTo(Screen.PaymentInitiated.route) { inclusive = true }
                    }
                },
                onEchec = {
                    // Le worker a fini en FAILED -> Direction le même écran mais configuré en échec
                    navController.navigate(Screen.PaymentSuccess.route) {
                        popUpTo(Screen.PaymentInitiated.route) { inclusive = true }
                    }
                }
            )
        }

        // ── PAYMENT SUCCESS / FAILED (Écran partagé) ───────────────────────────
        composable(Screen.PaymentSuccess.route) {
            // 🛠️ On lit l'état final directement depuis ton sharedUiState
            val isPaymentValid = sharedUiState.isPaySuccess == PaymentStatus.SUCCESS

            PaymentSuccessScreen(
                isSuccess = isPaymentValid,
                errorMsg  = sharedUiState.error, // Transmet la vraie erreur (ex: solde insuffisant) si elle existe
                onCloseClick = {
                    distributorViewModel.cleanPayment()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onReservationsClick = {
                    if (isPaymentValid) {
                        // Si succès -> On l'envoie voir ses réservations
                        distributorViewModel.cleanPayment()
                        navController.navigate("${Screen.Home.route}/${Tab.RESERVATIONS.label}") {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    } else {
                        distributorViewModel.cleanPayment()
                        navController.popBackStack(Screen.Payment.route, false)
                    }
                }
            )
        }

        // ── PROFILE ───────────────────────────────────────────────────────────
        composable(Screen.GazProfile.route) {
            GazProfileScreen(
                onBackClick = {
                    navController.navigate("${Screen.Home.route}/${Tab.ACCOUNT.label}") {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onSaved = {
                    navController.navigate("${Screen.Home.route}/${Tab.ACCOUNT.label}") {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }


}