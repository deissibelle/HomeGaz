package cm.horion.homegaz.presentation.ui.pages.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import cm.horion.homegaz.domain.model.common.Screen
import cm.horion.homegaz.presentation.ui.components.home.*
import cm.horion.homegaz.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

private sealed class PendingAction {
    object Refresh : PendingAction()
    data class ClickPoint(val pointId: String) : PendingAction()
}

@Composable
fun HomeScreen(
    viewModel         : HomeViewModel = koinViewModel(),
    navController     : NavController,
    onRouteClick      : (Double, Double) -> Unit,
    onRequestLocation : () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var pendingAction by remember { mutableStateOf<PendingAction?>(null) }


    LaunchedEffect(uiState.locationGranted) {
        if (uiState.locationGranted) {
            when (val action = pendingAction) {
                is PendingAction.Refresh    -> viewModel.loadPoints()
                is PendingAction.ClickPoint -> {
                    uiState.allPoints
                        .find { it.id == action.pointId }
                        ?.let { viewModel.onPointClick(it) }
                }
                null -> Unit
            }
            pendingAction = null
        }
    }


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        val route = navBackStackEntry?.destination?.route ?: return@LaunchedEffect
        if (route == Screen.Home.route || route.startsWith("${Screen.Home.route}/")) {
            viewModel.onDismissPopup()
        }
    }

    val runWithLocation = { action: PendingAction ->
        if (uiState.locationGranted) {
            when (action) {
                is PendingAction.Refresh    -> viewModel.loadPoints()
                is PendingAction.ClickPoint -> {
                    uiState.allPoints
                        .find { it.id == action.pointId }
                        ?.let { viewModel.onPointClick(it) }
                }
            }
        } else {
            pendingAction = action
            onRequestLocation()
        }
    }


    var markerScreenX by remember { mutableStateOf<Float?>(null) }
    var markerScreenY by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(uiState.selectedPoint?.id) {
        if (uiState.selectedPoint == null) {
            markerScreenX = null
            markerScreenY = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        InteractiveMap(
            modifier               = Modifier.fillMaxSize(),
            points                 = uiState.filteredPoints,
            selectedPoint          = uiState.selectedPoint,
            locationGranted        = uiState.locationGranted,
            userLat                = uiState.userLat,
            userLng                = uiState.userLng,
            routePoints            = uiState.routePolyline,
            routeBoundingBox       = uiState.routeBoundingBox,
            onPointClick           = { point -> viewModel.onPointClick(point) },
            onDismissPopup         = viewModel::onDismissPopup,
            onMarkerScreenPosition = { x, y ->
                markerScreenX = x
                markerScreenY = y
            },
            onRecenterClick = {
                if (!uiState.locationGranted) onRequestLocation()
            }
        )

        HomeFilterCard(
            modifier            = Modifier.align(Alignment.TopCenter),
            distributor         = uiState.selectedDistributor,
            onDistributorChange = viewModel::onDistributorChange,
            distance            = uiState.selectedDistance,
            onDistanceChange    = viewModel::onDistanceChange,
            weight              = uiState.selectedWeight,
            onWeightChange      = viewModel::onWeightChange,
            onRefresh           = { runWithLocation(PendingAction.Refresh) },
            distributorOptions  = listOf("SCTM", "Tradex", "Total", "Glocal Gaz"),
            distanceOptions     = listOf("100 mètre", "500 mètre", "1 km", "5 km", "10 km"),
            weightOptions       = listOf("6kg", "12.5kg", "28kg")
        )


        val selectedPoint = uiState.selectedPoint
        val sx = markerScreenX
        val sy = markerScreenY

        if (selectedPoint != null && sx != null && sy != null) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val density = LocalDensity.current

                val cardWidthPx  = with(density) { 186.dp.toPx() }
                val cardHeightPx = with(density) { 220.dp.toPx() }
                val marginPx     = with(density) { 4.dp.toPx() }

                val offsetXPx = (sx - cardWidthPx - marginPx)
                    .coerceAtLeast(0f)
                    .coerceAtMost(with(density) { maxWidth.toPx() } - cardWidthPx)

                val offsetYPx = (sy - cardHeightPx / 2f)
                    .coerceAtLeast(0f)
                    .coerceAtMost(with(density) { maxHeight.toPx() } - cardHeightPx)

                Box(
                    modifier = Modifier.offset(
                        x = with(density) { offsetXPx.toDp() },
                        y = with(density) { offsetYPx.toDp() }
                    )
                ) {
                    DistributionPointSheet(
                        point      = selectedPoint,
                        onBuyClick = {
                            viewModel.onDismissPopup()
                            navController.navigate(
                                Screen.DistributorDetail.createRoute(selectedPoint.id)
                            )
                        },
                        onRouteClick = {
                            if (uiState.locationGranted) {

                                viewModel.calculateRouteToPoint(
                                    selectedPoint.latitude,
                                    selectedPoint.longitude
                                )
                            } else {
                                pendingAction = PendingAction.ClickPoint(selectedPoint.id)
                                onRequestLocation()
                            }
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