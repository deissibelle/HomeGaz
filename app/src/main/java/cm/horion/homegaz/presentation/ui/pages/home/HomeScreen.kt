package cm.horion.homegaz.presentation.ui.pages.home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import cm.horion.homegaz.domain.model.common.Screen
import cm.horion.homegaz.domain.model.consommateur.dto.Company
import cm.horion.homegaz.domain.model.consommateur.dto.GazSize
import cm.horion.homegaz.domain.model.consommateur.dto.GazType
import cm.horion.homegaz.presentation.ui.components.home.*
import cm.horion.homegaz.presentation.ui.theme.LocalThemeIsDark
import cm.horion.homegaz.presentation.viewmodel.ConsumerViewModel
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

    // Extraction dynamique des distributeurs proposant du BUTANE
    val distributorOptions = remember(uiState.availableBottles) {
        val butaneBottles = uiState.availableBottles.filter { it.gazType == GazType.BUTANE }

        if (butaneBottles.isEmpty()) {
            Company.entries.map { it.name }
        } else {
            butaneBottles.map { it.company.name }.distinct()
        }
    }

    // Extraction dynamique des tailles/poids disponibles en BUTANE
    val weightOptions = remember(uiState.availableBottles, uiState.selectedDistributor) {
        var butaneBottles = uiState.availableBottles.filter { it.gazType == GazType.BUTANE }

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
        mapController.onPointClick   = { point -> consumerViewModel.onPointClick(point) }
        mapController.onDismissPopup = { consumerViewModel.onDismissPopup() }
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

    val isDark = LocalThemeIsDark.current
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "HomeGaz",
            modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleLarge,
            color = if (isDark) Color.White else MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Box(modifier = Modifier.fillMaxSize()) {

            InteractiveMap(
                controller        = mapController,
                points            = uiState.allPoints,
                selectedPoint     = uiState.selectedPoint,
                locationGranted   = uiState.locationGranted,
                selectedDistance  = uiState.selectedDistance,
                userLat           = uiState.userLat,
                userLng           = uiState.userLng,
                routePoints       = uiState.routePolyline,
                routeBoundingBox  = uiState.routeBoundingBox,
                onRecenterClick   = { if (!uiState.locationGranted) onRequestLocation() },
                fetch             = { consumerViewModel.fetch() },
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

                    if (weight == "Tous") {
                        consumerViewModel.onBattleUuidChange("")
                    } else {
                        val matchingBottle = uiState.availableBottles.find { "${it.gazSize.size} kg" == weight }
                        matchingBottle?.let { consumerViewModel.onBattleUuidChange(it.uuid) }
                    }
                },
                onRefresh           = { runWithLocation(PendingAction.Refresh) },
                distributorOptions  = distributorOptions,
                distanceOptions     = listOf("100 mètre", "500 mètre", "1 km", "5 km", "10 km"),
                weightOptions       = weightOptions
            )


            uiState.selectedPoint?.let { selectedPoint ->
                DistributionPointBottomSheet(
                    point = selectedPoint,
                    onDismissRequest = {
                        consumerViewModel.onDismissPopup()
                    },
                    onBuyClick = {
                        consumerViewModel.onDismissPopup()
                        navController.navigate(Screen.DistributorDetail.createRoute(selectedPoint.enterpriseUuid!!))
                    },
                    onRouteClick = {
                        if (uiState.locationGranted) {
                            consumerViewModel.calculateRouteToPoint(
                                selectedPoint.address.location.latitude,
                                selectedPoint.address.location.longitude
                            )
                        } else {
                            pendingAction = PendingAction.ClickPoint(selectedPoint.id)
                            onRequestLocation()
                        }
                    }
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.error?.let { errorMsg ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(errorMsg)
                }
            }
        }
    }
}