package cm.horion.homegaz.presentation.ui.components.home

import android.graphics.PointF
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.home.DistributorPoint
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider

private val YAOUNDE_CENTER = Point(3.848, 11.502)
private const val DEFAULT_ZOOM = 13f

@Composable
fun InteractiveMap(
    points                 : List<DistributorPoint>,
    selectedPoint          : DistributorPoint?,
    onPointClick           : (DistributorPoint) -> Unit,
    onDismissPopup         : () -> Unit,
    locationGranted        : Boolean = false,
    userLat                : Double? = null,
    userLng                : Double? = null,
    routePoints            : List<Point> = emptyList(),
    onRecenterClick        : () -> Unit = {},
    onMarkerScreenPosition : (x: Float, y: Float) -> Unit = { _, _ -> },
    modifier               : Modifier = Modifier
) {
    val context        = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    var mapViewRef           by remember { mutableStateOf<MapView?>(null) }
    var userLocationLayerRef by remember { mutableStateOf<UserLocationLayer?>(null) }
    val latestOnMarkerPos   = rememberUpdatedState(onMarkerScreenPosition)
    val latestSelectedPoint = rememberUpdatedState(selectedPoint)
    val latestOnDismiss     = rememberUpdatedState(onDismissPopup)

    val mapStyleJson = remember {
        context.resources.openRawResource(R.raw.map_style)
            .bufferedReader()
            .use { it.readText() }
    }

    // Cycle de vie MapKit
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
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Couche GPS selon permission
    LaunchedEffect(locationGranted) {
        userLocationLayerRef?.isVisible           = locationGranted
        userLocationLayerRef?.isHeadingModeActive = locationGranted
    }

    // Centrage initial sur la vraie position utilisateur
    var hasInitiallyCentered by remember { mutableStateOf(false) }
    LaunchedEffect(userLat, userLng) {
        if (!hasInitiallyCentered && userLat != null && userLng != null) {
            mapViewRef?.mapWindow?.map?.move(
                CameraPosition(Point(userLat, userLng), 15f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 0.8f),
                null
            )
            hasInitiallyCentered = true
        }
    }

    //  Animation vers le point sélectionné
    LaunchedEffect(selectedPoint?.id) {
        selectedPoint?.let { pt ->
            mapViewRef?.mapWindow?.map?.move(
                CameraPosition(Point(pt.latitude, pt.longitude), 15f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 0.4f),
                null
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).also { mv ->
                    mapViewRef = mv
                    val map = mv.mapWindow.map

                    map.setMapStyle(mapStyleJson)
                    map.move(CameraPosition(YAOUNDE_CENTER, DEFAULT_ZOOM, 0f, 0f))

                    val kit   = MapKitFactory.getInstance()
                    val layer = kit.createUserLocationLayer(mv.mapWindow)
                    userLocationLayerRef          = layer
                    layer.isVisible               = locationGranted
                    layer.isHeadingModeActive     = locationGranted
                    setupUserLocationLayer(mv, layer)

                    map.addInputListener(object : InputListener {
                        override fun onMapTap(m: Map, pt: Point) {
                            latestOnDismiss.value()
                        }
                        override fun onMapLongTap(m: Map, pt: Point) = Unit
                    })

                    // CameraListener à chaque frame
                    map.addCameraListener { _, _, _, _ ->
                        latestSelectedPoint.value?.let { sp ->
                            val screen = mv.mapWindow.worldToScreen(
                                Point(sp.latitude, sp.longitude)
                            ) ?: return@addCameraListener
                            latestOnMarkerPos.value(screen.x, screen.y)
                        }
                    }
                }
            },

            update = { mv ->
                val map = mv.mapWindow.map
                map.mapObjects.clear()

                // ── Itinéraire réel (polyline depuis DrivingRouter)
                if (routePoints.size >= 2) {
                    val poly = map.mapObjects.addPolyline(Polyline(routePoints))
                    poly.strokeWidth  = 6f
                    poly.setStrokeColor(0xFF003761.toInt())
                    poly.outlineWidth = 2f
                    poly.setOutlineColor(0xFFFFFFFF.toInt())
                }

                //Marqueurs des distributeurs
                points.forEach { point ->
                    addDistributorMarker(
                        mv           = mv,
                        map          = map,
                        point        = point,
                        isSelected   = point.id == selectedPoint?.id,
                        onPointClick = onPointClick,
                        onScreenPos  = onMarkerScreenPosition
                    )
                }
                // Position immédiate après update
                selectedPoint?.let { sp ->
                    val screen = mv.mapWindow.worldToScreen(
                        Point(sp.latitude, sp.longitude)
                    ) ?: return@let
                    onMarkerScreenPosition(screen.x, screen.y)
                }
            }
        )

        // Bouton Recentrer
        RecenterButton(
            onClick = {
                val mv     = mapViewRef ?: return@RecenterButton
                val target = if (userLat != null && userLng != null)
                    Point(userLat, userLng) else YAOUNDE_CENTER
                val zoom   = if (userLat != null) 15f else DEFAULT_ZOOM
                mv.mapWindow.map.move(
                    CameraPosition(target, zoom, 0f, 0f),
                    Animation(Animation.Type.SMOOTH, 0.5f),
                    null
                )
                onRecenterClick()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        )
    }
}

private fun addDistributorMarker(
    mv          : MapView,
    map         : Map,
    point       : DistributorPoint,
    isSelected  : Boolean,
    onPointClick: (DistributorPoint) -> Unit,
    onScreenPos : (Float, Float) -> Unit
) {
    val placemark = map.mapObjects.addPlacemark()
    placemark.geometry = Point(point.latitude, point.longitude)

    placemark.setIcon(
        ImageProvider.fromResource(mv.context, R.drawable.marker),
        IconStyle().apply {
            anchor = PointF(0.5f, 1.0f)
            scale  = if (isSelected) 1.4f else 1.0f
            zIndex = if (isSelected) 100f else 0f
        }
    )

    placemark.setText(
        point.name,
        TextStyle().apply {
            size         = 11f
            color        = 0xFF003761.toInt()
            outlineColor = 0xFFFFFFFF.toInt()
            outlineWidth = 2f
            placement    = TextStyle.Placement.TOP
            offset       = 5f
        }
    )
    placemark.addTapListener { _, _ ->
        onPointClick(point)
        // Position calculée immédiatement au tap, avant l'animation de caméra
        val screen = mv.mapWindow.worldToScreen(Point(point.latitude, point.longitude))
        if (screen != null) onScreenPos(screen.x, screen.y)
        true
    }
}