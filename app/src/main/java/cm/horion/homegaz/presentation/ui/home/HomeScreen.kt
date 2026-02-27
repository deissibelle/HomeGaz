@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
package cm.horion.homegaz.presentation.ui.home

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cm.horion.homegaz.domain.model.DistributionPoint
import cm.horion.homegaz.presentation.ui.components.home.HomeFilterCard
import cm.horion.homegaz.presentation.ui.components.home.InteractiveMap
import cm.horion.homegaz.presentation.ui.components.home.DistributionPointSheet
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

// Données de test locales
private val DISTRIBUTION_POINTS = listOf(
    DistributionPoint("1", "SCTM Bastos", "", 3.882, 11.514),
    DistributionPoint("2", "Total Melen", "", 3.861, 11.521),
    DistributionPoint("3", "Tradex Centre", "", 3.848, 11.502)
)

private val DISTRIBUTOR_OPTIONS = listOf("SCTM", "Tradex", "Total")
private val DISTANCE_OPTIONS = listOf("1 km", "5 km", "10 km")
private val WEIGHT_OPTIONS = listOf("6kg", "12kg", "38kg")

@Composable
fun HomeScreen(
    onMarkerClick: () -> Unit = {}
) {
    var selectedDistributor by remember { mutableStateOf(DISTRIBUTOR_OPTIONS[0]) }
    var selectedDistance by remember { mutableStateOf(DISTANCE_OPTIONS[0]) }
    var selectedWeight by remember { mutableStateOf(WEIGHT_OPTIONS[1]) }
    var selectedPoint by remember { mutableStateOf<DistributionPoint?>(null) }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Carte Google Maps
        InteractiveMap(
            points = DISTRIBUTION_POINTS,
            onPointClick = { point ->
                selectedPoint = point
            }
        )
        // 2. Overlay des Filtres
        HomeFilterCard(
            distributor = selectedDistributor,
            onDistributorChange = { selectedDistributor = it },
            distance = selectedDistance,
            onDistanceChange = { selectedDistance = it },
            weight = selectedWeight,
            onWeightChange = { selectedWeight = it },
            onRefresh = { },
            distributorOptions = DISTRIBUTOR_OPTIONS,
            distanceOptions = DISTANCE_OPTIONS,
            weightOptions = WEIGHT_OPTIONS
        )
    }

    // 3. Détails du point
    if (selectedPoint != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedPoint = null }
        ) {
            DistributionPointSheet(point = selectedPoint!!)
        }
    }
}