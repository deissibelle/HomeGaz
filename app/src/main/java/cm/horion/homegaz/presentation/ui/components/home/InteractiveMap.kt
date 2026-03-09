package cm.horion.homegaz.presentation.ui.components.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.home.DistributionPoint
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*

@Composable
fun InteractiveMap(
    points: List<DistributionPoint>,
    selectedPoint: DistributionPoint?,
    onPointClick: (DistributionPoint) -> Unit,
    onDismissPopup: () -> Unit,
    locationGranted: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val mapProperties = MapProperties(
        isMyLocationEnabled = locationGranted,
    )

    val uiSettings = MapUiSettings(
        zoomControlsEnabled = false,
        myLocationButtonEnabled = locationGranted
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(3.848, 11.502), 12f)
    }

    LaunchedEffect(selectedPoint) {
        selectedPoint?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
            )
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = uiSettings,
        onMapClick = { onDismissPopup() }
    ) {
        points.forEach { point ->
            Marker(
                state = rememberMarkerState(
                    position = LatLng(point.latitude, point.longitude)
                ),
                onClick = {
                    onPointClick(point)
                    true
                }
            )
        }
    }
}