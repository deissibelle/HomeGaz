package cm.horion.homegaz.presentation.ui.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.home.DistributorPoint
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.*

@Composable
fun InteractiveMap(
    points          : List<DistributorPoint>,
    selectedPoint   : DistributorPoint?,
    onPointClick    : (DistributorPoint) -> Unit,
    onDismissPopup  : () -> Unit,
    locationGranted : Boolean = false,
    userLat         : Double? = null,
    userLng         : Double? = null,
    userPhotoUrl    : String? = null,
    routePoints     : List<LatLng> = emptyList(),
    onRecenterClick : () -> Unit = {},
    modifier        : Modifier = Modifier
) {
    val context = LocalContext.current

    val mapStyle = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(3.848, 11.502), 14f)
    }

    LaunchedEffect(userLat, userLng) {
        if (userLat != null && userLng != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(userLat, userLng), 14f)
            )
        }
    }

    LaunchedEffect(selectedPoint) {
        selectedPoint?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        GoogleMap(
            modifier            = modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties          = MapProperties(isMyLocationEnabled = false, mapStyleOptions = mapStyle),
            uiSettings          = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false),
            onMapClick          = { onDismissPopup() }
        ) {
            if (routePoints.isNotEmpty()) {
                Polyline(
                    points = routePoints,
                    color = MaterialTheme.colorScheme.primary,
                    width = 12f,
                    jointType = JointType.ROUND,
                    startCap = RoundCap(),
                    endCap = RoundCap()
                )
            }
            points.forEach { point ->
                MarkerComposable(
                    state = rememberMarkerState(position = LatLng(point.latitude, point.longitude)),
                    onClick = { onPointClick(point); true }
                ) {
                    DistributorMarker(name = point.name, isSelected = point.id == selectedPoint?.id)
                }
            }

            if (locationGranted && userLat != null && userLng != null) {
                MarkerComposable(
                    state  = rememberMarkerState(
                        position = LatLng(userLat, userLng)
                    ),
                    anchor = androidx.compose.ui.geometry.Offset(0.5f, 1f)
                ) {
                    UserLocationMarker(photoUrl = userPhotoUrl)
                }
            }
        }

        RecenterButton(
            onClick  = onRecenterClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        )
    }
}