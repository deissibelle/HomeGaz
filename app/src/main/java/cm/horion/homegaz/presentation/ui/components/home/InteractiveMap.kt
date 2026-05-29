package cm.horion.homegaz.presentation.ui.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import cm.horion.homegaz.domain.model.home.DistributorPoint
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView

/**
 * Composable ultra-léger : ne fait QUE créer la MapView et la passer
 * au MapController. Toute la logique JNI est dans MapController.
 */
@Composable
fun InteractiveMap(
    controller             : MapController,
    points                 : List<DistributorPoint>,
    selectedPoint          : DistributorPoint?,
    locationGranted        : Boolean      = false,
    userLat                : Double?      = null,
    userLng                : Double?      = null,
    routePoints            : List<Point>  = emptyList(),
    routeBoundingBox       : BoundingBox? = null,
    onRecenterClick        : () -> Unit   = {},
    modifier               : Modifier     = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // Cycle de vie MapKit géré ici, mais les objets JNI vivent dans MapController
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> controller.onStart()
                Lifecycle.Event.ON_STOP  -> controller.onStop()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Centrage initial sur la position GPS (une seule fois)
    var hasInitiallyCentered by remember { mutableStateOf(false) }
    LaunchedEffect(userLat, userLng) {
        if (!hasInitiallyCentered && userLat != null && userLng != null) {
            controller.centerOn(userLat, userLng)
            hasInitiallyCentered = true
        }
    }

    // Synchro des données → MapController (pas de logique JNI ici)
    LaunchedEffect(locationGranted) {
        controller.setLocationEnabled(locationGranted)
    }

    LaunchedEffect(points) {
        controller.syncMarkers(points)
    }

    LaunchedEffect(selectedPoint?.id) {
        controller.setSelectedPoint(selectedPoint)
    }

    LaunchedEffect(routePoints) {
        controller.updateRoute(routePoints)
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory  = { ctx ->
                MapView(ctx).also { mv ->
                    controller.attachMapView(mv)
                }
            }
            // Pas de update{} — tout passe par les LaunchedEffect → MapController
        )

        RecenterButton(
            onClick  = {
                controller.recenter(userLat, userLng)
                onRecenterClick()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        )
    }
}