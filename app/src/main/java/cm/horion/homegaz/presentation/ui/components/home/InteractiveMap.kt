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
import cm.horion.homegaz.domain.model.distributor.dto.Distributor
import cm.horion.homegaz.domain.model.home.DistributorPoint
import cm.horion.homegaz.util.getCurrentLocation
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import kotlinx.coroutines.launch


@Composable
fun InteractiveMap(
    controller             : MapController,
    points                 : List<Distributor>,
    selectedPoint          : Distributor?,
    selectedDistance       : String,
    locationGranted        : Boolean      = false,
    userLat                : Double?      = null,
    userLng                : Double?      = null,
    routePoints            : List<Point>  = emptyList(),
    routeBoundingBox       : BoundingBox? = null,
    onRecenterClick        : () -> Unit   = {},
    fetch        : () -> Unit   = {},
    onLocationFetched      : (Double, Double) -> Unit = { _, _ -> },
    modifier               : Modifier     = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    //  On crée le scope pour pouvoir lancer des coroutines dans les onClick
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

    //  Gestion du Cycle de vie de MapKit
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
            //controller.destroy()
        }
    }

    // Remplace le LaunchedEffect actuel par deux effets séparés
    LaunchedEffect(locationGranted) {
        controller.setLocationEnabled(locationGranted)
    }

// Centre uniquement au premier fix valide (userLat passe de null à une valeur)
    val hascentered = remember { mutableStateOf(false) }
    LaunchedEffect(userLat, userLng) {
        if (userLat != null && userLng != null && !hascentered.value) {
            controller.centerOnRadius(userLat, userLng, currentRadiusMeters)
            hascentered.value = true
        } else if (userLat == null && userLng == null) {
            controller.centerOnRadius(3.848, 11.502, currentRadiusMeters)
        }
    }

// Recentre si la distance sélectionnée change
    LaunchedEffect(selectedDistance) {
        val lat = userLat ?: 3.848
        val lng = userLng ?: 11.502
        controller.centerOnRadius(lat, lng, currentRadiusMeters)
    }

    // Synchronisations vers le contrôleur
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

        // Bouton de recentrage
        RecenterButton(
            onClick = {
                onRecenterClick() // Gère la demande de permission

                if (locationGranted) {

                    fetch()

                    // Et recentre immédiatement sur les dernières données stables connues
                    if (userLat != null && userLng != null) {
                        controller.centerOnRadius(userLat, userLng, currentRadiusMeters)
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