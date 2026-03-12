package cm.horion.homegaz.presentation.ui.pages.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cm.horion.homegaz.domain.model.common.Screen
import cm.horion.homegaz.domain.model.distributor.DistributorProduct
import cm.horion.homegaz.domain.model.distributor.OrderSummary
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.domain.model.distributor.DeliveryOption
import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.presentation.ui.MainScreen
import cm.horion.homegaz.presentation.ui.Tab
import cm.horion.homegaz.presentation.ui.pages.confirmation.ConfirmationScreen
import cm.horion.homegaz.presentation.ui.pages.distributor.DistributorPointDetailScreen
import cm.horion.homegaz.presentation.ui.pages.location.LocationPermissionScreen
import cm.horion.homegaz.presentation.ui.pages.onboarding.OnboardingScreen
import cm.horion.homegaz.presentation.ui.pages.payment.PaymentInitiatedScreen
import cm.horion.homegaz.presentation.ui.pages.payment.PaymentScreen
import cm.horion.homegaz.presentation.ui.pages.payment.PaymentSuccessScreen
import com.google.android.gms.location.LocationServices

@Composable
fun HomeGazApp(userPrefs: UserPreferencesRepository) {

    val navController       = rememberNavController()
    val context             = LocalContext.current
    val onboardingCompleted by userPrefs.isOnboardingCompleted.collectAsState(initial = null)

    //Location
    var locationGranted by remember { mutableStateOf(false) }
    var returnedPointId by remember { mutableStateOf<String?>(null) }
    var returnedLat     by remember { mutableStateOf<Double?>(null) }
    var returnedLng     by remember { mutableStateOf<Double?>(null) }

    //Order state
    var currentProduct      by remember { mutableStateOf(DistributorProduct("", "", 0)) }
    var currentQuantity     by remember { mutableIntStateOf(1) }
    var currentDelivery     by remember { mutableStateOf(DeliveryOption.LIVRAISON) }
    var currentOrderSummary by remember { mutableStateOf<OrderSummary?>(null) }

    // Helpers
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

        // Onboarding
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Home
        composable(Screen.Home.route) {
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
            arguments = listOf(navArgument("initialTab") { type = NavType.StringType })
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

        composable(
            route     = Screen.DistributorDetail.route,
            arguments = listOf(navArgument("pointId") { type = NavType.StringType })
        ) { back ->
            val pointId = back.arguments?.getString("pointId")
            val product = DistributorProduct(brand = "SCTM", weight = "12,5kg", unitPrice = 7500)
            DistributorPointDetailScreen(
                product     = product,
                onBackClick = { navController.popBackStack() },
                onNextClick = { quantity, option ->
                    currentProduct  = product
                    currentQuantity = quantity
                    currentDelivery = option
                    navController.navigate(Screen.Payment.route)
                }
            )
        }

        composable(Screen.Payment.route) {
            PaymentScreen(
                brand          = currentProduct.brand,
                weight         = currentProduct.weight,
                quantity       = currentQuantity,
                deliveryOption = currentDelivery,
                unitPrice      = currentProduct.unitPrice,
                onBackClick    = { navController.popBackStack() },
                onNextClick    = { summary ->
                    currentOrderSummary = summary
                    navController.navigate(Screen.Confirmation.route)
                }
            )
        }

        // Confirmation
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
    }
}