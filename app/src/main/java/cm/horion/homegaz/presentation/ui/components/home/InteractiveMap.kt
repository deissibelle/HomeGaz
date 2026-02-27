package cm.horion.homegaz.presentation.ui.components.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import cm.horion.homegaz.domain.model.DistributionPoint

@Composable
fun InteractiveMap(
    points: List<DistributionPoint>,
    onPointClick: (DistributionPoint) -> Unit,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(3.848, 11.502), 12f)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true),
        uiSettings = MapUiSettings(zoomControlsEnabled = false)
    ) {
        points.forEach { point ->
            Marker(
                state = MarkerState(position = LatLng(point.latitude, point.longitude)),
                title = point.name,
                snippet = point.distributor,
                onClick = {
                    onPointClick(point)
                    true
                }
            )
        }
    }
}