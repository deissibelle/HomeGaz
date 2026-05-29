package cm.horion.homegaz.presentation.ui.pages.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ── MapController : créé une seule fois, vit tant que HomeScreen est en composition
    val mapController = remember { MapController(context) }

    var pendingAction by remember { mutableStateOf<PendingAction?>(null) }

    // States pour la position écran du marqueur sélectionné
    val markerScreenXState = remember { mutableStateOf<Float?>(null) }
    val markerScreenYState = remember { mutableStateOf<Float?>(null) }
    val markerScreenX by markerScreenXState
    val markerScreenY by markerScreenYState

    // Brancher les callbacks Compose → MapController APRÈS la déclaration des states
    // SideEffect s'exécute après chaque recomposition réussie
    SideEffect {
        mapController.onPointClick           = { point -> viewModel.onPointClick(point) }
        mapController.onDismissPopup         = { viewModel.onDismissPopup() }
        mapController.onMarkerScreenPosition = { x, y ->
            markerScreenXState.value = x
            markerScreenYState.value = y
        }
    }

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

    LaunchedEffect(uiState.selectedPoint?.id) {
        if (uiState.selectedPoint == null) {
            markerScreenXState.value = null
            markerScreenYState.value = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        InteractiveMap(
            controller       = mapController,
            points           = uiState.filteredPoints,
            selectedPoint    = uiState.selectedPoint,
            locationGranted  = uiState.locationGranted,
            selectedDistance = uiState.selectedDistance,
            userLat          = uiState.userLat,
            userLng          = uiState.userLng,
            routePoints      = uiState.routePolyline,
            routeBoundingBox = uiState.routeBoundingBox,
            onRecenterClick  = { if (!uiState.locationGranted) onRequestLocation() },
            modifier         = Modifier.fillMaxSize()
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

                // Récupération des dimensions de l'écran et de la carte en Pixels
                val screenWidthPx  = with(density) { maxWidth.toPx() }
                val screenHeightPx = with(density) { maxHeight.toPx() }
                val cardWidthPx    = with(density) { 186.dp.toPx() }
                val cardHeightPx   = with(density) { 220.dp.toPx() }
                val marginPx       = with(density) { 16.dp.toPx() } // Marge de confort accrue

                // 🚀 STRATÉGIE D'AFFICHAGE DYNAMIQUE SUR L'AXE X
                val offsetXPx = if (sx > cardWidthPx + marginPx * 2) {
                    // Option A : Il y a de la place à gauche, on l'affiche à gauche du marqueur
                    sx - cardWidthPx - marginPx
                } else if (sx + cardWidthPx + marginPx * 2 < screenWidthPx) {
                    // Option B : Le marqueur est trop à gauche, on bascule la fiche à DROITE du marqueur
                    sx + marginPx
                } else {
                    // Option C : Cas extrême (écran très étroit), on centre la fiche horizontalement
                    (screenWidthPx - cardWidthPx) / 2f
                }

                // 🚀 STRATÉGIE D'AFFICHAGE DYNAMIQUE SUR L'AXE Y
                // On centre verticalement par rapport au point, tout en limitant dans l'écran
                val offsetYPx = (sy - cardHeightPx / 2f)
                    .coerceAtLeast(marginPx) // Évite de coller ou dépasser en haut
                    .coerceAtMost(screenHeightPx - cardHeightPx - marginPx) // Évite de dépasser en bas

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