@file:OptIn(ExperimentalMaterial3Api::class)

package cm.horion.homegaz.presentation.ui.pages.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import cm.horion.homegaz.presentation.ui.components.home.DistributionPointSheet
import cm.horion.homegaz.presentation.ui.components.home.HomeFilterCard
import cm.horion.homegaz.presentation.ui.components.home.InteractiveMap
import cm.horion.homegaz.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    navController: NavController,
    onMarkerClick: (String) -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onRouteClick: (Double, Double) -> Unit,
    onBuyClick: (String) -> Unit = {},
    pendingPointId: String? = null,
    userLat: Double? = null,
    userLng: Double? = null,
    locationGranted: Boolean = false,
    locationDenied: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(locationGranted, userLat, userLng, pendingPointId) {
        if (locationGranted) viewModel.onLocationGranted(userLat, userLng, pendingPointId)
    }
    LaunchedEffect(locationDenied) {
        if (locationDenied) viewModel.onLocationDenied()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        InteractiveMap(
            points          = uiState.filteredPoints,
            selectedPoint   = uiState.selectedPoint,
            locationGranted = uiState.locationGranted,
            routePoints     = uiState.routePolyline,
            userLat         = uiState.userLat,
            userLng         = uiState.userLng,
            userPhotoUrl    = uiState.userPhotoUrl,
            onPointClick    = { point ->
                if (uiState.locationGranted) viewModel.onPointSelected(point)
                else onMarkerClick(point.id)
            },
            onDismissPopup  = viewModel::onPointDismissed,
            onRecenterClick = viewModel::onRecenter
        )

        HomeFilterCard(
            modifier            = Modifier.align(Alignment.TopCenter),
            distributor         = uiState.selectedDistributor,
            onDistributorChange = viewModel::onDistributorChange,
            distance            = uiState.selectedDistance,
            onDistanceChange    = viewModel::onDistanceChange,
            weight              = uiState.selectedWeight,
            onWeightChange      = viewModel::onWeightChange,
            onRefresh           = onRefreshClick,
            distributorOptions  = listOf("SCTM", "Tradex", "Total"),
            distanceOptions     = listOf("100 m", "500 m", "1 km", "5 km", "10 km"),
            weightOptions       = listOf("6kg", "12kg", "38kg")
        )

        if (uiState.locationGranted && uiState.selectedPoint != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                DistributionPointSheet(
                    point        = uiState.selectedPoint!!,
                    onBuyClick   = { onBuyClick(uiState.selectedPoint!!.id) },
                    onRouteClick = {
                        onRouteClick(
                            uiState.selectedPoint!!.latitude,
                            uiState.selectedPoint!!.longitude
                        )
                    })
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color    = MaterialTheme.colorScheme.primary
            )
        }
    }
}