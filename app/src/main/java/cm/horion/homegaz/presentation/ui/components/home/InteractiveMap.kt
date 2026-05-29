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
import kotlinx.coroutines.launch


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

    // 🚀 ICI : On crée le scope pour pouvoir lancer des coroutines dans les onClick
    val scope = rememberCoroutineScope()

    // On mémorise le rayon en mètres selon la chaîne sélectionnée
    val currentRadiusMeters = remember(selectedDistance) {
        when (selectedDistance) {
            "100 mètre" -> 100f
            "500 mètre" -> 500f
            "1 km"      -> 1000f
            "5 km"      -> 5000f
            "10 km"     -> 10000f
            else        -> 2000f
        }
    }

    // 1. Gestion du Cycle de vie de MapKit
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

    // 2. Écoute de la localisation et des filtres au démarrage/changement
    LaunchedEffect(locationGranted, selectedDistance) {
        controller.setLocationEnabled(locationGranted)

        if (locationGranted) {
            val location = getCurrentLocation()
            if (location != null) {
                controller.centerOnRadius(location.latitude, location.longitude, currentRadiusMeters)
                onLocationFetched(location.latitude, location.longitude)
            }
        }
    }

    // 3. Synchronisations vers le contrôleur
    LaunchedEffect(points) {
        controller.syncMarkers(points)
    }

    LaunchedEffect(selectedPoint?.id) {
        controller.setSelectedPoint(selectedPoint)
    }

    LaunchedEffect(routePoints) {
        controller.updateRoute(routePoints)
    }

    // 4. Layout
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory  = { ctx ->
                MapView(ctx).also { mv ->
                    controller.attachMapView(mv)
                }
            }
        )

        // 5. Bouton de recentrage corrigé
        RecenterButton(
            onClick  = {
                onRecenterClick() // Demande la permission si nécessaire

                if (locationGranted) {
                    // 🚀 ✅ CORRECT : On ouvre un bloc coroutine asynchrone sécurisé pour le clic
                    scope.launch {
                        val location = getCurrentLocation()
                        if (location != null) {
                            controller.centerOnRadius(location.latitude, location.longitude, currentRadiusMeters)
                        } else {
                            controller.recenter(userLat, userLng)
                        }
                    }
                } else {
                    controller.recenter(null, null)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        )
    }
}