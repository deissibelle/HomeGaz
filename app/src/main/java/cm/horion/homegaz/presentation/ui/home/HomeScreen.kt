@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
package cm.horion.homegaz.presentation.ui.home

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.domain.DistributionPoint
import cm.horion.homegaz.domain.UiMarker
import cm.horion.homegaz.presentation.ui.components.common.BottomNavBar
import cm.horion.homegaz.presentation.ui.components.home.HomeFilterCard
import cm.horion.homegaz.presentation.ui.components.home.InteractiveMap
import cm.horion.homegaz.presentation.ui.components.home.DistributionPointSheet
import cm.horion.homegaz.presentation.ui.theme.HomeGazTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState


private val MARKERS_LIST = listOf(
    UiMarker(299f, 185f, 31f, 38f),
    UiMarker(19f, 249f, 31f, 38f),
    UiMarker(168f, 223f, 42f, 51f),
    UiMarker(210f, 304f, 54f, 65f),
    UiMarker(14f, 445f, 42f, 51f),
    UiMarker(25f, 585f, 31f, 37f),
    UiMarker(181f, 604f, 31f, 37f),
    UiMarker(123f, 688f, 31f, 37f),
    UiMarker(255f, 695f, 31f, 37f),
)

private val DISTRIBUTOR_OPTIONS = listOf("SCTM", "Tradex", "Total")
private val DISTANCE_OPTIONS    = listOf("100 m", "500 m", "1 km")
private val WEIGHT_OPTIONS      = listOf("6kg", "12kg", "38kg")

@Composable
fun HomeScreen(
    onMarkerClick: () -> Unit = {}
) {
    var selectedDistributor by remember { mutableStateOf(DISTRIBUTOR_OPTIONS[0]) }
    var selectedDistance    by remember { mutableStateOf(DISTANCE_OPTIONS[0]) }
    var selectedWeight      by remember { mutableStateOf(WEIGHT_OPTIONS[1]) }
    var selectedPoint       by remember { mutableStateOf<DistributionPoint?>(null) }


    val isPreview = LocalInspectionMode.current
    val permissionState = if (!isPreview) {
        rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    } else null

    Scaffold(
        bottomBar = { BottomNavBar() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Carte Interactive
            InteractiveMap(
                markers = MARKERS_LIST,
                onMarkerClick = { marker ->
                    onMarkerClick()
                },
                modifier = Modifier.fillMaxSize()
            )

            // Carte des Filtres
            HomeFilterCard(
                distributor = selectedDistributor,
                onDistributorChange = { selectedDistributor = it },
                distance = selectedDistance,
                onDistanceChange = { selectedDistance = it },
                weight = selectedWeight,
                onWeightChange = { selectedWeight = it },
                onRefresh = { /* Logique de rafraîchissement ici */ },
                distributorOptions = DISTRIBUTOR_OPTIONS,
                distanceOptions = DISTANCE_OPTIONS,
                weightOptions = WEIGHT_OPTIONS
            )
        }
    }
    if (selectedPoint != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedPoint = null },
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            DistributionPointSheet(point = selectedPoint!!)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeGazTheme { HomeScreen() }
}