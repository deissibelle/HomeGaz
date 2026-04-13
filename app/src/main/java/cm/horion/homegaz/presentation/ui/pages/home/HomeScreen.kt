package cm.horion.homegaz.presentation.ui.pages.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import cm.horion.homegaz.domain.model.common.Screen
import cm.horion.homegaz.presentation.ui.components.home.*
import cm.horion.homegaz.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    navController: NavController,
    onRouteClick: (Double, Double) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

    Box(modifier = Modifier.fillMaxSize()) {

        InteractiveMap(
            points = uiState.filteredPoints,
            selectedPoint = uiState.selectedPoint,
            locationGranted = uiState.locationGranted,
            userLat = uiState.userLat,
            userLng = uiState.userLng,
            onPointClick = { point ->
                runWithLocation { viewModel.onPointClick(point) }
            },
            onDismissPopup = viewModel::onDismissPopup
        )
        HomeFilterCard(
            modifier = Modifier.align(Alignment.TopCenter),
            distributor = uiState.selectedDistributor,
            onDistributorChange = viewModel::onDistributorChange,
            distance = uiState.selectedDistance,
            onDistanceChange = viewModel::onDistanceChange,
            weight = uiState.selectedWeight,
            onWeightChange = viewModel::onWeightChange,
            onRefresh = {
                runWithLocation { viewModel.loadPoints() }
            },
            distributorOptions = listOf("SCTM", "Tradex", "Total", "Glocal Gaz"),
            distanceOptions = listOf("1 km", "5 km", "10 km"),
            weightOptions = listOf("6kg", "12.5kg", "28kg")
        )

        if (uiState.selectedPoint != null && uiState.locationGranted) {
            val point = uiState.selectedPoint!!
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            ) {
                DistributionPointSheet(
                    point = point,
                    onBuyClick = {
                        navController.navigate(Screen.DistributorDetail.createRoute(point.id))
                    },
                    onRouteClick = {
                        onRouteClick(point.latitude, point.longitude)
                    }
                )
            }
        }
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}