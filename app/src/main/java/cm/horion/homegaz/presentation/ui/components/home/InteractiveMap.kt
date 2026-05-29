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
import cm.horion.homegaz.util.getCurrentLocation
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView


@Composable
fun InteractiveMap(
    controller             : MapController,
    points                 : List<DistributorPoint>,
    selectedPoint          : DistributorPoint?,
    selectedDistance       : String,
    locationGranted        : Boolean      = false,
    userLat                : Double?      = null,
    userLng                : Double?      = null,
    routePoints            : List<Point>  = emptyList(),
    routeBoundingBox       : BoundingBox? = null,
    onRecenterClick        : () -> Unit   = {},
    onLocationFetched      : (Double, Double) -> Unit = { _, _ -> },
    modifier               : Modifier     = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

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

    // 🚀 L'APPEL MANQUANT : Déclenche la recherche GPS dès que la permission est acquise
//    LaunchedEffect(locationGranted) {
//        controller.setLocationEnabled(locationGranted)
//
//        if (locationGranted) {
//            // On appelle ta fonction suspendue en tâche de fond
//            val location = getCurrentLocation()
//            if (location != null) {
//                // 1. On informe le MapController de déplacer la caméra sur le champ
//                controller.updateUserLocation(location)
//                // 2. On remonte l'info au parent/ViewModel au cas où il en a besoin pour recalculer des distances
//                onLocationFetched(location.latitude, location.longitude)
//            }
//        }
//    }

    LaunchedEffect(locationGranted, selectedDistance) {
        controller.setLocationEnabled(locationGranted)

        if (locationGranted) {
            val location = getCurrentLocation()
            if (location != null) {
                // 1. Calcul du zoom dynamique en fonction de la distance sélectionnée
                val targetZoom = when (selectedDistance) {
                    "100 mètre" -> 18.5f // Très serré, rayon d'environ 100-150m autour de toi
                    "500 mètre" -> 16.2f // Rayon d'environ 500m visible à l'écran
                    "1 km"      -> 15.2f // Rayon de 1km visible
                    "5 km"      -> 13.0f // Permet de voir à 5km aux alentours
                    "10 km"     -> 11.8f // Échelle globale de la ville de Yaoundé
                    else        -> 14.0f
                }

                // 2. On déplace la caméra sur ta position AVEC le bon niveau de zoom
                controller.centerOn(location.latitude, location.longitude, targetZoom)

                // 3. On remonte l'info au ViewModel si besoin
                onLocationFetched(location.latitude, location.longitude)
            }
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