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
                consumerViewModel.onDistributorChange(brand)
            },
            distance            = uiState.selectedDistance,
            onDistanceChange    = { dist -> consumerViewModel.onDistanceChange(dist) },
            weight              = uiState.selectedWeight,
            onWeightChange      = { weight ->
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
}

//@Composable
//fun HomeScreen(
//    viewModel         : HomeViewModel = koinViewModel(),
//    consumerViewModel : ConsumerViewModel = koinViewModel(),
//    navController     : NavController,
//    onRouteClick      : (Double, Double) -> Unit,
//    onRequestLocation : () -> Unit
//) {
//    val context = LocalContext.current
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//    val consumerUiState = consumerViewModel.uiState
//
//    // ── MapController : créé une seule fois, vit tant que HomeScreen est en composition
//    val mapController = remember { MapController(context) }
//
//    var pendingAction by remember { mutableStateOf<PendingAction?>(null) }
//
//    // States pour la position écran du marqueur sélectionné
//    val markerScreenXState = remember { mutableStateOf<Float?>(null) }
//    val markerScreenYState = remember { mutableStateOf<Float?>(null) }
//    val markerScreenX by markerScreenXState
//    val markerScreenY by markerScreenYState
//
//    // Brancher les callbacks Compose → MapController APRÈS la déclaration des states
//    // SideEffect s'exécute après chaque recomposition réussie
//    SideEffect {
//        mapController.onPointClick           = { point -> consumerViewModel.onPointClick(point) }
//        mapController.onDismissPopup         = { consumerViewModel.onDismissPopup() }
//        mapController.onMarkerScreenPosition = { x, y ->
//            markerScreenXState.value = x
//            markerScreenYState.value = y
//        }
//    }
//
//    LaunchedEffect(uiState.locationGranted) {
//        if (uiState.locationGranted) {
//            when (val action = pendingAction) {
//                is PendingAction.Refresh    -> viewModel.loadPoints()
//                is PendingAction.ClickPoint -> {
//                    uiState.allPoints
//                        .find { it.id == action.pointId }
//                        ?.let { viewModel.onPointClick(it) }
//                }
//                null -> Unit
//            }
//            pendingAction = null
//        }
//    }
//
//    val runWithLocation = { action: PendingAction ->
//        if (uiState.locationGranted) {
//            when (action) {
//                is PendingAction.Refresh    -> viewModel.loadPoints()
//                is PendingAction.ClickPoint -> {
//                    uiState.allPoints
//                        .find { it.id == action.pointId }
//                        ?.let { viewModel.onPointClick(it) }
//                }
//            }
//        } else {
//            pendingAction = action
//            onRequestLocation()
//        }
//    }
//
//    LaunchedEffect(uiState.selectedPoint?.id) {
//        if (uiState.selectedPoint == null) {
//            markerScreenXState.value = null
//            markerScreenYState.value = null
//        }
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        InteractiveMap(
//            controller       = mapController,
//            points           = uiState.filteredPoints,
//            point           = consumerUiState.allPoints,
//            selectedPoint    = uiState.selectedPoint,
//            selectedPoints    = consumerUiState.selectedPoint,
//            locationGranted  = uiState.locationGranted,
//            selectedDistance = consumerUiState.selectedDistance,
//            userLat          = consumerUiState.userLat,
//            userLng          = consumerUiState.userLng,
//            routePoints      = uiState.routePolyline,
//            routeBoundingBox = uiState.routeBoundingBox,
//            onRecenterClick  = { if (!uiState.locationGranted) onRequestLocation() },
//            onLocationFetched = { lat, lng ->
//                consumerViewModel.onLocationChanged(lat ,lng )
//            },
//            modifier         = Modifier.fillMaxSize()
//        )
//
//        HomeFilterCard(
//            modifier            = Modifier.align(Alignment.TopCenter),
//            distributor         = uiState.selectedDistributor,
//            onDistributorChange = viewModel::onDistributorChange,
//            distance            = consumerUiState.selectedDistance,
//            onDistanceChange    = consumerViewModel::onDistanceChange,
//            weight              = uiState.selectedWeight,
//            onWeightChange      = viewModel::onWeightChange,
//            onRefresh           = { runWithLocation(PendingAction.Refresh) },
//            distributorOptions  = listOf("SCTM", "Tradex", "Total", "Glocal Gaz"),
//            distanceOptions     = listOf("100 mètre", "500 mètre", "1 km", "5 km", "10 km"),
//            weightOptions       = listOf("6kg", "12.5kg", "28kg")
//        )
//
//        val selectedPoint = uiState.selectedPoint
//        val sx = markerScreenX
//        val sy = markerScreenY
//
//        if (selectedPoint != null && sx != null && sy != null) {
//            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
//                val density = LocalDensity.current
//
//                val screenWidthPx  = with(density) { maxWidth.toPx() }
//                val screenHeightPx = with(density) { maxHeight.toPx() }
//                val cardWidthPx    = with(density) { 186.dp.toPx() }
//                val cardHeightPx   = with(density) { 220.dp.toPx() }
//                val marginPx       = with(density) { 16.dp.toPx() }
//
//                // 🚀 1. On détermine d'abord l'état d'ancrage visuel
//                val isMarkerOnRight = sx > cardWidthPx + marginPx * 2
//
//                // 🚀 2. Calcul dynamique de l'axe X
//                val offsetXPx = if (isMarkerOnRight) {
//                    // Option A : La fiche est à gauche, donc le marqueur est à sa DROITE
//                    sx - cardWidthPx - marginPx
//                } else if (sx + cardWidthPx + marginPx * 2 < screenWidthPx) {
//                    // Option B : La fiche est à droite, donc le marqueur est à sa GAUCHE
//                    sx + marginPx
//                } else {
//                    // Option C : Écran trop étroit, centré
//                    (screenWidthPx - cardWidthPx) / 2f
//                }
//
//                // 🚀 3. Calcul dynamique de l'axe Y
//                val offsetYPx = (sy - cardHeightPx / 2f)
//                    .coerceAtLeast(marginPx)
//                    .coerceAtMost(screenHeightPx - cardHeightPx - marginPx)
//
//                Box(
//                    modifier = Modifier.offset(
//                        x = with(density) { offsetXPx.toDp() },
//                        y = with(density) { offsetYPx.toDp() }
//                    )
//                ) {
//                    DistributionPointSheet(
//                        point      = selectedPoint,
//                        isMarkerOnRight = isMarkerOnRight,
//                        onBuyClick = {
//                            viewModel.onDismissPopup()
//                            navController.navigate(
//                                Screen.DistributorDetail.createRoute(selectedPoint.id)
//                            )
//                        },
//                        onRouteClick = {
//                            if (uiState.locationGranted) {
//                                viewModel.calculateRouteToPoint(
//                                    selectedPoint.latitude,
//                                    selectedPoint.longitude
//                                )
//                            } else {
//                                pendingAction = PendingAction.ClickPoint(selectedPoint.id)
//                                onRequestLocation()
//                            }
//                        }
//                    )
//                }
//            }
//        }
//
//        if (uiState.isLoading) {
//            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//        }
//
//        uiState.error?.let { errorMsg ->
//            Snackbar(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(16.dp)
//            ) { Text(errorMsg) }
//        }
//    }
//}