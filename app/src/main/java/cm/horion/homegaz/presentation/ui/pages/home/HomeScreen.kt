package cm.horion.homegaz.presentation.ui.pages.home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import cm.horion.homegaz.domain.model.common.Screen
import cm.horion.homegaz.domain.model.consommateur.dto.Company
import cm.horion.homegaz.domain.model.consommateur.dto.GazSize
import cm.horion.homegaz.domain.model.consommateur.dto.GazType
import cm.horion.homegaz.presentation.ui.components.home.*
import cm.horion.homegaz.presentation.viewmodel.ConsumerViewModel
import cm.horion.homegaz.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

private sealed class PendingAction {
    object Refresh : PendingAction()
    data class ClickPoint(val pointId: String) : PendingAction()
}

@Composable
fun HomeScreen(
    consumerViewModel : ConsumerViewModel = koinViewModel(),
    navController     : NavController,
    onRouteClick      : (Double, Double) -> Unit,
    onRequestLocation : () -> Unit
) {
    val context = LocalContext.current
    val uiState by consumerViewModel.uiState.collectAsState()

    val mapController = remember { MapController(context) }
    var pendingAction by remember { mutableStateOf<PendingAction?>(null) }

    val markerScreenXState = remember { mutableStateOf<Float?>(null) }
    val markerScreenYState = remember { mutableStateOf<Float?>(null) }
    val markerScreenX by markerScreenXState
    val markerScreenY by markerScreenYState

    // 🚀 1. Extraction dynamique des distributeurs proposant du BUTANE
    val distributorOptions = remember(uiState.availableBottles) {
        val butaneBottles = uiState.availableBottles.filter { it.gazType == GazType.BUTANE }

        if (butaneBottles.isEmpty()) {
            Company.entries.map { it.name }
        } else {
            butaneBottles.map { it.company.name }.distinct()
        }
    }

// 🚀 2. Extraction dynamique des tailles/poids disponibles en BUTANE
    val weightOptions = remember(uiState.availableBottles, uiState.selectedDistributor) {
        // On filtre d'abord par BUTANE
        var butaneBottles = uiState.availableBottles.filter { it.gazType == GazType.BUTANE }

        // Optionnel : Si un distributeur est déjà sélectionné, on ne montre que ses tailles à lui
        if (uiState.selectedDistributor.isNotEmpty()) {
            butaneBottles = butaneBottles.filter { it.company.name == uiState.selectedDistributor }
        }

        if (butaneBottles.isEmpty()) {
            GazSize.entries.map { "${it.size} kg" }
        } else {
            butaneBottles.map { "${it.gazSize.size} kg" }.distinct()
        }
    }



    SideEffect {
        mapController.onPointClick           = { point -> consumerViewModel.onPointClick(point) }
        mapController.onDismissPopup         = { consumerViewModel.onDismissPopup() }
        mapController.onMarkerScreenPosition = { x, y ->
            markerScreenXState.value = x
            markerScreenYState.value = y
        }
    }

    LaunchedEffect(uiState.locationGranted) {
        if (uiState.locationGranted) {
            when (val action = pendingAction) {
                is PendingAction.Refresh    -> consumerViewModel.fetch()
                is PendingAction.ClickPoint -> {
                    uiState.allPoints
                        .find { it.id == action.pointId }
                        ?.let { consumerViewModel.onPointClick(it) }
                }
                null -> Unit
            }
            pendingAction = null
        }
    }

    val runWithLocation = { action: PendingAction ->
        if (uiState.locationGranted) {
            when (action) {
                is PendingAction.Refresh    -> consumerViewModel.fetch()
                is PendingAction.ClickPoint -> {
                    uiState.allPoints
                        .find { it.id == action.pointId }
                        ?.let { consumerViewModel.onPointClick(it) }
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
    val isDark = isSystemInDarkTheme()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "HomeGaz",
            modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleLarge,
            color = if (isDark) Color.White else MaterialTheme.colorScheme.primary,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    Box(modifier = Modifier.fillMaxSize()) {

        InteractiveMap(
            controller        = mapController,
            points            = uiState.allPoints, // Branchement direct sur tes listes d'API
            selectedPoint     = uiState.selectedPoint,
            locationGranted   = uiState.locationGranted,
            selectedDistance  = uiState.selectedDistance,
            userLat           = uiState.userLat,
            userLng           = uiState.userLng,
            routePoints       = uiState.routePolyline,
            routeBoundingBox  = uiState.routeBoundingBox,
            onRecenterClick   = { if (!uiState.locationGranted) onRequestLocation() },
            onLocationFetched = { lat, lng -> consumerViewModel.onLocationChanged(lat, lng) },
            modifier          = Modifier.fillMaxSize()
        )

        HomeFilterCard(
            modifier            = Modifier.align(Alignment.TopCenter),
            distributor         = uiState.selectedDistributor,
            onDistributorChange = { brand ->
                consumerViewModel.onDismissPopup()
                consumerViewModel.onDistributorChange(brand)
            },
            distance            = uiState.selectedDistance,
            onDistanceChange    = { dist ->
                consumerViewModel.onDismissPopup()
                consumerViewModel.onDistanceChange(dist)
            },
            weight              = uiState.selectedWeight,
            onWeightChange      = { weight ->
                consumerViewModel.onDismissPopup()
                consumerViewModel.onWeightChange(weight)

                // 🚀 3. Met à jour le battleUuid du ViewModel pour cibler la bonne bouteille sur l'API
                if (weight == "Tous") {
                    consumerViewModel.onBattleUuidChange("")
                } else {
                    val matchingBottle = uiState.availableBottles.find { "${it.gazSize.size} kg" == weight }
                    matchingBottle?.let { consumerViewModel.onBattleUuidChange(it.uuid) }
                }
            },
            onRefresh           = { runWithLocation(PendingAction.Refresh) },
            distributorOptions  = distributorOptions, // ✅ Dynamique
            distanceOptions     = listOf("100 mètre", "500 mètre", "1 km", "5 km", "10 km"),
            weightOptions       = weightOptions       // ✅ Dynamique ("6.5 kg", etc.)
        )

        // --- Reste de ton code pour le DistributionPointSheet (Inchangé et parfaitement fonctionnel) ---
        val selectedPoint = uiState.selectedPoint
        val sx = markerScreenX
        val sy = markerScreenY

        if (selectedPoint != null && sx != null && sy != null) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val density = LocalDensity.current
                val screenWidthPx  = with(density) { maxWidth.toPx() }
                val screenHeightPx = with(density) { maxHeight.toPx() }
                val cardWidthPx    = with(density) { 186.dp.toPx() }
                val cardHeightPx   = with(density) { 220.dp.toPx() }
                val marginPx       = with(density) { 16.dp.toPx() }

                val isMarkerOnRight = sx > cardWidthPx + marginPx * 2
                val offsetXPx = if (isMarkerOnRight) sx - cardWidthPx - marginPx else if (sx + cardWidthPx + marginPx * 2 < screenWidthPx) sx + marginPx else (screenWidthPx - cardWidthPx) / 2f
                val offsetYPx = (sy - cardHeightPx / 2f).coerceAtLeast(marginPx).coerceAtMost(screenHeightPx - cardHeightPx - marginPx)

                Box(modifier = Modifier.offset(x = with(density) { offsetXPx.toDp() }, y = with(density) { offsetYPx.toDp() })) {
                    DistributionPointSheet(
                        point = selectedPoint,
                        isMarkerOnRight = isMarkerOnRight,
                        onBuyClick = {
                            consumerViewModel.onDismissPopup()
                            navController.navigate(Screen.DistributorDetail.createRoute(selectedPoint.enterpriseUuid!!))
                        },
                        onRouteClick = {
                            if (uiState.locationGranted) {
                                consumerViewModel.calculateRouteToPoint(selectedPoint.address.location.latitude, selectedPoint.address.location.longitude)
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
            Snackbar(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)) { Text(errorMsg) }
        }
    }
}}