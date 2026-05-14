package cm.horion.homegaz.presentation.ui.components.home

import android.graphics.PointF
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import cm.horion.homegaz.domain.model.home.DistributorPoint
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import cm.horion.homegaz.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.ScreenPoint

@Composable
fun InteractiveMap(
    points                 : List<DistributorPoint>,
    selectedPoint          : DistributorPoint?,
    onPointClick           : (DistributorPoint) -> Unit,
    onDismissPopup         : () -> Unit,
    locationGranted        : Boolean = false,
    userLat                : Double? = null,
    userLng                : Double? = null,
    userPhotoUrl           : String? = null,
    routePoints            : List<Point> = emptyList(),
    onRecenterClick        : () -> Unit = {},
    onMarkerScreenPosition : (x: Float, y: Float) -> Unit = { _, _ -> },
    modifier               : Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // Référence stable à la MapView créée une seule fois
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    // Gérer le cycle de vie de la carte
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    MapKitFactory.getInstance().onStart()
                    mapViewRef?.onStart()
                }
                Lifecycle.Event.ON_STOP -> {
                    MapKitFactory.getInstance().onStop()
                    mapViewRef?.onStop()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Animer la caméra vers le point sélectionné
    LaunchedEffect(selectedPoint) {
        selectedPoint?.let { point ->
            mapViewRef?.mapWindow?.map?.move(
                CameraPosition(
                    Point(point.latitude, point.longitude),
                    15f, 0f, 0f
                ),
                Animation(Animation.Type.SMOOTH, 0.4f),
                null
            )
        }
    }


    Box(modifier = modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                MapView(context).also { mv ->
                    mapViewRef = mv
                    val map = mv.mapWindow.map

                    // Position initiale : Yaoundé
                    map.move(
                        CameraPosition(Point(3.848, 11.502), 13f, 0f, 0f)
                    )

                    // Listener de tap sur la carte pour fermer le popup
                    map.addInputListener(object : InputListener {
                        override fun onMapTap(m: Map, point: Point) {
                            onDismissPopup()
                        }
                        override fun onMapLongTap(m: Map, point: Point) {}
                    })

                    // Listener caméra pour recalculer la position écran du marqueur sélectionné
                    map.addCameraListener { _, _, _, finished ->
                        if (finished) {
                            selectedPoint?.let { sp ->
                                val screenPt: ScreenPoint = mv.mapWindow.worldToScreen(
                                    Point(sp.latitude, sp.longitude)
                                ) ?: return@addCameraListener
                                onMarkerScreenPosition(screenPt.x, screenPt.y)
                            }
                        }
                    }
                }
            },
            update = { mv ->
                val map = mv.mapWindow.map


                map.mapObjects.clear()


                if (routePoints.isNotEmpty()) {
                    val polylineObj = map.mapObjects.addPolyline(
                        com.yandex.mapkit.geometry.Polyline(routePoints)
                    )
                    polylineObj.strokeWidth = 5f
                    polylineObj.setStrokeColor(0xFF4CAF50.toInt())
                }

                points.forEach { point ->
                    val placemark = map.mapObjects.addPlacemark()
                    placemark.geometry = Point(point.latitude, point.longitude)

                    placemark.setIcon(
                        ImageProvider.fromResource(mv.context, R.drawable.marker),
                        IconStyle().apply {
                            anchor = PointF(0.5f, 1.0f)
                            scale  = if (point.id == selectedPoint?.id) 1.4f else 1.0f
                        }
                    )

                    placemark.addTapListener { _, _ ->
                        onPointClick(point)
                        // Calculer la position écran immédiatement après le tap
                        val screenPt: ScreenPoint = mv.mapWindow.worldToScreen(
                            Point(point.latitude, point.longitude)
                        ) ?: return@addTapListener true
                        onMarkerScreenPosition(screenPt.x, screenPt.y)
                        true
                    }
                }


                if (locationGranted && userLat != null && userLng != null) {
                    val userPlacemark = map.mapObjects.addPlacemark()
                    userPlacemark.geometry = Point(userLat, userLng)
                    userPlacemark.setIcon(
                        ImageProvider.fromResource(mv.context, R.drawable.profil),
                        IconStyle().apply {
                            anchor = PointF(0.5f, 1.0f)
                            scale  = 0.8f
                        }
                    )
                }

                selectedPoint?.let { sp ->
                    val screenPt: ScreenPoint = mv.mapWindow.worldToScreen(
                        Point(sp.latitude, sp.longitude)
                    ) ?: return@let
                    onMarkerScreenPosition(screenPt.x, screenPt.y)
                }
            }
        )

        // Bouton de recalage automatique
        RecenterButton(
            onClick  = onRecenterClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        )
    }
}