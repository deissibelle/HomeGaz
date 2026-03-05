@file:OptIn(ExperimentalMaterial3Api::class)
package cm.horion.homegaz.presentation.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    userLng: Double? = null,
    locationGranted: Boolean = false,
    locationDenied: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(locationGranted) {
        if (locationGranted) {
            viewModel.onLocationGranted(lat = userLat, lng = userLng, pointId = pendingPointId)
        }
    }
    LaunchedEffect(locationDenied) {
        if (locationDenied) viewModel.onLocationDenied()
    }
    Box(modifier = Modifier.fillMaxSize()) {

        InteractiveMap(
            points = uiState.filteredPoints,
            onPointClick = { point ->
                if (!uiState.locationGranted) {
                    onMarkerClick(point.id)
                } else {
                    viewModel.onLocationGranted(userLat, userLng, point.id)
                }
            }
        )

        HomeFilterCard(
            distributor = uiState.selectedDistributor,
            onDistributorChange = { viewModel.onDistributorChange(it) },
            distance = uiState.selectedDistance,
            onDistanceChange = { viewModel.onDistanceChange(it) },
            weight = uiState.selectedWeight,
            onWeightChange = { viewModel.onWeightChange(it) },
            onRefresh = {
                if (!uiState.locationGranted) onRefreshClick()
                else viewModel.onLocationGranted(userLat, userLng)
            },
            distributorOptions = listOf( "SCTM", "Tradex", "Total"),
            distanceOptions = listOf("1 km", "5 km", "10 km"),
            weightOptions = listOf("6kg", "12kg", "38kg")
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    uiState.selectedPoint?.let { point ->
        ModalBottomSheet(onDismissRequest = { viewModel.onPointDismissed() }) {
            DistributionPointSheet(point = point)
        }
    }
}