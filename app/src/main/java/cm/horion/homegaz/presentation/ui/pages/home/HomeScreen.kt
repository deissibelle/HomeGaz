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
import androidx.navigation.compose.rememberNavController
import cm.horion.homegaz.domain.model.home.DistributionPoint
import cm.horion.homegaz.presentation.ui.components.home.DistributionPointSheet
import cm.horion.homegaz.presentation.ui.components.home.HomeFilterCard
import cm.horion.homegaz.presentation.ui.components.home.InteractiveMap
import cm.horion.homegaz.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onMarkerClick: (pointId: String) -> Unit = {},
    onRefreshClick: () -> Unit = {},
    pendingPointId: String? = null,
    userLat: Double? = null,
    navController: NavController,
    userLng: Double? = null,
    locationGranted: Boolean = false,
    locationDenied: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(locationGranted, userLat, userLng, pendingPointId) {
        if (locationGranted) {
            viewModel.onLocationGranted(
                lat = userLat,
                lng = userLng,
                pointId = pendingPointId
            )
        }
    }

    LaunchedEffect(locationDenied) {
        if (locationDenied) viewModel.onLocationDenied()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        InteractiveMap(
            points = uiState.filteredPoints,
            selectedPoint = uiState.selectedPoint,
            locationGranted = uiState.locationGranted,
            onPointClick = { point ->
                if (uiState.locationGranted) {
                    viewModel.onPointSelected(point)
                } else {
                    onMarkerClick(point.id)
                }
            },
            onDismissPopup = { viewModel.onPointDismissed() }
        )

        HomeFilterCard(
            distributor = uiState.selectedDistributor,
            onDistributorChange = { viewModel.onDistributorChange(it) },
            distance = uiState.selectedDistance,
            onDistanceChange = { viewModel.onDistanceChange(it) },
            weight = uiState.selectedWeight,
            onWeightChange = { viewModel.onWeightChange(it) },
            onRefresh = {
                onRefreshClick()
            },
            distributorOptions = listOf("SCTM", "Tradex", "Total"),
            distanceOptions = listOf("1 km", "5 km", "10 km"),
            weightOptions = listOf("6kg", "12kg", "38kg")
        )

        if (uiState.locationGranted && uiState.selectedPoint != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                DistributionPointSheet(
                    point = uiState.selectedPoint!!,
                    onBuyClick = {      navController.navigate("distributor_detail/${uiState.selectedPoint!!.id}") },
                    onRouteClick = { /* Intent Google Maps */ }
                )
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}