package cm.horion.homegaz.presentation.ui.pages.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cm.horion.homegaz.domain.model.common.Screen
import cm.horion.homegaz.domain.model.distributor.OrderSummary
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.domain.model.distributor.DeliveryOption
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
import com.google.android.gms.location.LocationServices
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeGazApp(userPrefs: UserPreferencesRepository) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val onboardingCompleted by userPrefs.isOnboardingCompleted.collectAsState(initial = null)
    //helper navigate account
    fun goToAccountTab() {
        navController.navigate("${Screen.Home.route}/${Tab.ACCOUNT.label}") {
            popUpTo(Screen.Home.route) { inclusive = false }
        }
    }
    // Location
    var locationGranted by remember { mutableStateOf(false) }
    var returnedPointId by remember { mutableStateOf<String?>(null) }
    var returnedLat     by remember { mutableStateOf<Double?>(null) }
    var returnedLng     by remember { mutableStateOf<Double?>(null) }

    //  Order state

    var currentBrand        by remember { mutableStateOf("") }
    var currentWeight       by remember { mutableStateOf("") }
    var currentUnitPrice    by remember { mutableIntStateOf(0) }
    var currentQuantity     by remember { mutableIntStateOf(1) }
    var currentDelivery     by remember { mutableStateOf(DeliveryOption.LIVRAISON) }
    var currentOrderSummary by remember { mutableStateOf<OrderSummary?>(null) }

    //Helpers
    fun fetchLocationAndGoHome(pId: String?) {
        LocationServices.getFusedLocationProviderClient(context)
            .lastLocation
            .addOnSuccessListener { loc ->
                returnedLat     = loc?.latitude
                returnedLng     = loc?.longitude
                returnedPointId = if (pId == "none") null else pId
                locationGranted = true
                navController.popBackStack(Screen.Home.route, false)
            }
    }

    fun navigateToPermissionOrFetch(pointId: String) {
        val granted = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) fetchLocationAndGoHome(pointId)
        else navController.navigate("${Screen.LocationPermission.route}/$pointId")
    }

    if (onboardingCompleted == null) return

    NavHost(
        navController    = navController,
        startDestination = if (onboardingCompleted == true) Screen.Home.route
        else Screen.Onboarding.route
    ) {

        //  Onboarding
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        //  Home
        composable(
            Screen.Home.route,
            popEnterTransition = {
                fadeIn(animationSpec = tween(1500, easing = EaseIn))
            }
        ) {
            MainScreen(
                navController   = navController,
                locationGranted = locationGranted,
                pendingPointId  = returnedPointId,
                userLat         = returnedLat,
                userLng         = returnedLng,
                onMarkerClick   = { navigateToPermissionOrFetch(it) },
                onRefreshClick  = { navigateToPermissionOrFetch("none") },
                onBuyClick      = { pointId ->
                    navController.navigate(Screen.DistributorDetail.createRoute(pointId))
                }
            )
        }

        composable(
            route     = "${Screen.Home.route}/{initialTab}",
            arguments = listOf(navArgument("initialTab") { type = NavType.StringType }),
            popEnterTransition = {
                fadeIn(animationSpec = tween(1500, easing = EaseIn))
            }
        ) { back ->
            val initialTab = back.arguments?.getString("initialTab") ?: Tab.HOME.label
            MainScreen(
                navController   = navController,
                locationGranted = locationGranted,
                pendingPointId  = returnedPointId,
                userLat         = returnedLat,
                userLng         = returnedLng,
                initialTab      = initialTab,
                onMarkerClick   = { navigateToPermissionOrFetch(it) },
                onRefreshClick  = { navigateToPermissionOrFetch("none") },
                onBuyClick      = { pointId ->
                    navController.navigate(Screen.DistributorDetail.createRoute(pointId))
                }
            )
        }

        // Location Permission
        composable(
            route     = "${Screen.LocationPermission.route}/{pointId}",
            arguments = listOf(navArgument("pointId") { type = NavType.StringType })
        ) { back ->
            val pId = back.arguments?.getString("pointId")
            LocationPermissionScreen(
                onPermissionGranted = {
                    val ok = ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    if (ok) fetchLocationAndGoHome(pId) else navController.popBackStack()
                },
                onPermissionDenied = { navController.popBackStack() }
            )
        }

        //  Distributor Detail
        composable(
            route     = Screen.DistributorDetail.route,
            arguments = listOf(navArgument("pointId") { type = NavType.StringType }),
            enterTransition = {
                fadeIn(animationSpec = tween(1500, easing = EaseIn)) +
                        scaleIn(initialScale = 0.92f, animationSpec = tween(600, easing = EaseIn))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(1500, easing = EaseIn)) +
                        scaleOut(targetScale = 0.92f, animationSpec = tween(600, easing = EaseIn))
            }
        ) { back ->
            val pointId = back.arguments?.getString("pointId") ?: return@composable


            val homeViewModel: HomeViewModel = koinViewModel()
            val homeState by homeViewModel.uiState.collectAsState()
            val point = homeState.allPoints.find { it.id == pointId } ?: return@composable

            DistributorPointDetailScreen(
                point       = point,
                onBackClick = { navController.popBackStack() },
                onNextClick = { quantity, option ->
                    // On mémorise uniquement ce dont PaymentScreen a besoin
                    currentBrand     = point.distributor
                    currentWeight    = point.weight
                    currentUnitPrice = point.priceXaf
                    currentQuantity  = quantity
                    currentDelivery  = option
                    navController.navigate(Screen.Payment.route)
                }
            )
        }

        //  Payment
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

        //  Confirmation
        composable(Screen.Confirmation.route) {
            val summary = currentOrderSummary ?: return@composable
            ConfirmationScreen(
                summary        = summary,
                onBackClick    = { navController.popBackStack() },
                onModifyClick  = {
                    navController.popBackStack(
                        route     = Screen.DistributorDetail.route,
                        inclusive = false
                    )
                },
                onConfirmClick = {
                    navController.navigate(Screen.PaymentInitiated.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }

        // Payment Initiated
        composable(Screen.PaymentInitiated.route) {
            PaymentInitiatedScreen(
                paymentMethod = currentOrderSummary?.paymentMethod ?: PaymentMethod.ORANGE_MONEY,
                onDone        = {
                    navController.navigate(Screen.PaymentSuccess.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }

        //Payment Success
        composable(Screen.PaymentSuccess.route) {
            PaymentSuccessScreen(
                onCloseClick        = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onReservationsClick = {
                    navController.navigate("${Screen.Home.route}/${Tab.RESERVATIONS.label}") {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }
        composable(
            route = Screen.GazProfile.route,
            enterTransition = {
                fadeIn(animationSpec = tween(400, easing = EaseIn)) +
                        scaleIn(initialScale = 0.96f, animationSpec = tween(400, easing = EaseIn))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300, easing = EaseIn)) +
                        scaleOut(targetScale = 0.96f, animationSpec = tween(300, easing = EaseIn))
            }
        ) {
            GazProfileScreen(
                onBackClick = { goToAccountTab() },
                onSaved     = { goToAccountTab() }
            )
        }

    }
}