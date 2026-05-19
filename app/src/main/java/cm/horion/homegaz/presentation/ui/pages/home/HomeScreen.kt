package cm.horion.homegaz.presentation.ui.pages.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import cm.horion.homegaz.domain.model.common.Screen
import cm.horion.homegaz.presentation.ui.components.home.*
import cm.horion.homegaz.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel    : HomeViewModel = koinViewModel(),
    navController: NavController,
    onRouteClick : (Double, Double) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    //  Dismiss popup quand on revient sur cet écran
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onDismissPopup()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    //  Launcher permission localisation
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        viewModel.onLocationPermissionResult(permissions.values.all { it })
    }

    val runWithLocation = { action: () -> Unit ->
        if (uiState.locationGranted) {
            action()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    //  Position écran du marqueur sélectionné
    var markerScreenX by remember { mutableStateOf(0f) }
    var markerScreenY by remember { mutableStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize()) {

        //  Carte
        InteractiveMap(
            modifier               = Modifier.fillMaxSize(),
            points                 = uiState.filteredPoints,
            selectedPoint          = uiState.selectedPoint,
            locationGranted        = uiState.locationGranted,
            userLat                = uiState.userLat,
            userLng                = uiState.userLng,
            routePoints            = uiState.routePolyline,
            onPointClick           = { point -> runWithLocation { viewModel.onPointClick(point) } },
            onDismissPopup         = viewModel::onDismissPopup,
            onMarkerScreenPosition = { x, y ->
                markerScreenX = x
                markerScreenY = y
            }
        )

        // Filtre en haut
        HomeFilterCard(
            modifier            = Modifier.align(Alignment.TopCenter),
            distributor         = uiState.selectedDistributor,
            onDistributorChange = viewModel::onDistributorChange,
            distance            = uiState.selectedDistance,
            onDistanceChange    = viewModel::onDistanceChange,
            weight              = uiState.selectedWeight,
            onWeightChange      = viewModel::onWeightChange,
            onRefresh           = { runWithLocation { viewModel.loadPoints() } },
            distributorOptions  = listOf("SCTM", "Tradex", "Total", "Glocal Gaz"),
            distanceOptions     = listOf("1 km", "5 km", "10 km"),
            weightOptions       = listOf("6kg", "12.5kg", "28kg")
        )


        if (uiState.selectedPoint != null && uiState.locationGranted) {
            val point = uiState.selectedPoint!!

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val density = LocalDensity.current

                val cardWidthPx  = with(density) { 186.dp.toPx() }
                val cardHeightPx = with(density) { 220.dp.toPx() }
                val marginPx     = with(density) { 8.dp.toPx() }

                val offsetXPx = (markerScreenX - cardWidthPx - marginPx)
                    .coerceAtLeast(0f)
                    .coerceAtMost(with(density) { maxWidth.toPx() } - cardWidthPx)

                val offsetYPx = (markerScreenY - cardHeightPx / 2f)
                    .coerceAtLeast(0f)
                    .coerceAtMost(with(density) { maxHeight.toPx() } - cardHeightPx)

                val offsetXDp = with(density) { offsetXPx.toDp() }
                val offsetYDp = with(density) { offsetYPx.toDp() }

                Box(modifier = Modifier.offset(x = offsetXDp, y = offsetYDp)) {
                    DistributionPointSheet(
                        point        = point,
                        onBuyClick   = {
                            viewModel.onDismissPopup()
                            navController.navigate(
                                Screen.DistributorDetail.createRoute(point.id)
                            )
                        },
                        onRouteClick = {
                            viewModel.calculateRouteToPoint(point.latitude, point.longitude)
                        }
                    )
                }
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        uiState.error?.let { errorMsg ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) { Text(errorMsg) }
        }
    }
}