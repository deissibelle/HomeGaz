package cm.horion.homegaz.presentation.ui.navigation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cm.horion.homegaz.domain.model.common.Screen
import cm.horion.homegaz.domain.model.distributor.DeliveryOption
import cm.horion.homegaz.domain.model.distributor.OrderSummary
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.presentation.ui.MainScreen
import cm.horion.homegaz.presentation.ui.Tab
import cm.horion.homegaz.presentation.ui.pages.confirmation.ConfirmationScreen
import cm.horion.homegaz.presentation.ui.pages.distributor.DistributorPointDetailScreen
import cm.horion.homegaz.presentation.ui.pages.gazprofile.GazProfileScreen
import cm.horion.homegaz.presentation.ui.pages.location.LocationPermissionScreen
import cm.horion.homegaz.presentation.ui.pages.onboarding.OnboardingScreen
import cm.horion.homegaz.presentation.ui.pages.payment.PaymentInitiatedScreen
import cm.horion.homegaz.presentation.ui.pages.payment.PaymentScreen
import cm.horion.homegaz.presentation.ui.pages.payment.PaymentSuccessScreen
import cm.horion.homegaz.presentation.viewmodel.HomeViewModel
import cm.horion.homegaz.presentation.viewmodel.ReservationsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeGazApp(userPrefs: UserPreferencesRepository) {

    val navController = rememberNavController()

    val onboardingCompleted by userPrefs
        .isOnboardingCompleted
        .collectAsState(initial = null)

    if (onboardingCompleted == null) return

    val startDestination = if (onboardingCompleted == true) {
        Screen.Home.route
    } else {
        Screen.Onboarding.route
    }

    val homeViewModel: HomeViewModel               = koinViewModel()
    val reservationsViewModel: ReservationsViewModel = koinViewModel()
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    var currentBrand        by remember { mutableStateOf("") }
    var currentWeight       by remember { mutableStateOf("") }
    var currentUnitPrice    by remember { mutableIntStateOf(0) }
    var currentQuantity     by remember { mutableIntStateOf(1) }
    var currentDelivery     by remember { mutableStateOf(DeliveryOption.LIVRAISON) }
    var currentOrderSummary by remember { mutableStateOf<OrderSummary?>(null) }

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
                onRouteClick          = { lat, lng ->
                    homeViewModel.calculateRouteToPoint(lat, lng)
                },
                onRequestLocation = {
                    navController.navigate(Screen.LocationPermission.route) {
                        launchSingleTop = true
                    }
                }
            )
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
                initialTab            = initialTab,
                onRouteClick          = { lat, lng ->
                    homeViewModel.calculateRouteToPoint(lat, lng)
                },
                onRequestLocation = {
                    navController.navigate(Screen.LocationPermission.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // ── DISTRIBUTOR DETAIL ────────────────────────────────────────────────
        composable(
            route           = Screen.DistributorDetail.route,
            arguments       = listOf(navArgument("pointId") { type = NavType.StringType }),
            enterTransition = { fadeIn(tween(400)) + scaleIn(initialScale = 0.92f) }
        ) { backStackEntry ->
            val pointId = backStackEntry.arguments?.getString("pointId") ?: ""
            val point   = uiState.allPoints.find { it.id == pointId }
            if (point != null) {
                DistributorPointDetailScreen(
                    point       = point,
                    onBackClick = { navController.popBackStack() },
                    onNextClick = { quantity, deliveryOption ->
                        currentBrand     = point.distributor
                        currentWeight    = point.weight
                        currentUnitPrice = point.priceXaf
                        currentQuantity  = quantity
                        currentDelivery  = deliveryOption
                        navController.navigate(Screen.Payment.route)
                    }
                )
            }
        }

        // ── PAYMENT ───────────────────────────────────────────────────────────
        composable(Screen.Payment.route) {
            PaymentScreen(
                brand          = currentBrand,
                weight         = currentWeight,
                quantity       = currentQuantity,
                deliveryOption = currentDelivery,
                unitPrice      = currentUnitPrice,
                onBackClick    = { navController.popBackStack() },
                onNextClick    = { summary ->
                    currentOrderSummary = summary
                    navController.navigate(Screen.Confirmation.route)
                }
            )
        }

        // ── CONFIRMATION ──────────────────────────────────────────────────────
        composable(Screen.Confirmation.route) {
            currentOrderSummary?.let { summary ->
                ConfirmationScreen(
                    summary        = summary,
                    onBackClick    = { navController.popBackStack() },
                    onModifyClick  = {
                        navController.popBackStack(Screen.DistributorDetail.route, false)
                    },
                    onConfirmClick = {
                        navController.navigate(Screen.PaymentInitiated.route)
                    }
                )
            }
        }

        // ── PAYMENT INITIATED ─────────────────────────────────────────────────
        composable(Screen.PaymentInitiated.route) {
            PaymentInitiatedScreen(
                paymentMethod = currentOrderSummary?.paymentMethod ?: PaymentMethod.OM,
                onDone        = { navController.navigate(Screen.PaymentSuccess.route) }
            )
        }

        // ── PAYMENT SUCCESS ───────────────────────────────────────────────────
        composable(Screen.PaymentSuccess.route) {
            PaymentSuccessScreen(
                onCloseClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onReservationsClick = {
                    navController.navigate("${Screen.Home.route}/${Tab.RESERVATIONS.label}")
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