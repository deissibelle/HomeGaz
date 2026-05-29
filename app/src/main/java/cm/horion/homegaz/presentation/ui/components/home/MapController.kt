package cm.horion.homegaz.presentation.ui.components.home

import android.content.Context
import android.graphics.PointF
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.home.DistributorPoint
import com.yandex.mapkit.Animation
import com.yandex.mapkit.ConflictResolutionMode
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

/**
 * Objet Java/Kotlin pur — PAS un Composable.
 * Toutes les références JNI (listeners, collections, placemarks) vivent ici,
 * dans un objet dont le cycle de vie est contrôlé manuellement par l'Activity/Fragment,
 * jamais par le runtime Compose.
 *
 * Callbacks vers Compose : simples lambdas var, remplacées sans toucher au JNI.
 */
class MapController(private val context: Context) {

    // ── Callbacks vers le monde Compose (remplacés librement, jamais GC par MapKit) ──
    var onPointClick           : ((DistributorPoint) -> Unit)? = null
    var onDismissPopup         : (() -> Unit)?                 = null
    var onMarkerScreenPosition : ((Float, Float) -> Unit)?     = null

    // ── Références JNI stables ────────────────────────────────────────────────────────
    private var mapView            : MapView?             = null
    private var markersCollection  : MapObjectCollection? = null
    private var routesCollection   : MapObjectCollection? = null
    private var userLocationLayer  : UserLocationLayer?   = null

    private val markerImageProvider : ImageProvider by lazy {
        ImageProvider.fromResource(context, R.drawable.marker)
    }
    private val circleImageProvider : ImageProvider by lazy {
        ImageProvider.fromResource(context, R.drawable.ic_circle)
    }

    // markersMap vit dans cette classe Java → le GC ne peut PAS le collecter
    // tant que MapController lui-même est vivant
    private val markersMap = mutableMapOf<String, MarkerEntry>()

    // Listeners déclarés comme champs de classe → référence forte, jamais GC
    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            onDismissPopup?.invoke()
        }
        override fun onMapLongTap(map: Map, point: Point) = Unit
    }

    private val cameraListener = CameraListener { _, _, reason, _ ->
        if (reason == CameraUpdateReason.GESTURES) {
            onDismissPopup?.invoke()
        } else {
            currentSelectedPoint?.let { sp ->
                mapView?.mapWindow
                    ?.worldToScreen(Point(sp.latitude, sp.longitude))
                    ?.let { onMarkerScreenPosition?.invoke(it.x, it.y) }
            }
        }
    }

    private var currentSelectedPoint : DistributorPoint? = null
    private var mapStyleJson          : String?           = null

    // ── Cycle de vie ──────────────────────────────────────────────────────────────────

    fun attachMapView(mv: MapView) {
        if (mapView != null) return  // déjà initialisé, on ne réinitialise pas

        mapView = mv

        if (mapStyleJson == null) {
            mapStyleJson = context.resources
                .openRawResource(R.raw.map_style)
                .bufferedReader()
                .use { it.readText() }
        }

        val map = mv.mapWindow.map
        map.setMapStyle(mapStyleJson!!)
        map.mapObjects.conflictResolutionMode = ConflictResolutionMode.MAJOR
        map.move(CameraPosition(YAOUNDE_CENTER, DEFAULT_ZOOM, 0f, 0f))

        markersCollection = map.mapObjects.addCollection()
        routesCollection  = map.mapObjects.addCollection()

        map.addInputListener(inputListener)
        map.addCameraListener(cameraListener)

        userLocationLayer = MapKitFactory.getInstance()
            .createUserLocationLayer(mv.mapWindow)
    }

    fun onStart() {
        MapKitFactory.getInstance().onStart()
        mapView?.onStart()
    }

    fun onStop() {
        MapKitFactory.getInstance().onStop()
        mapView?.onStop()
    }

    fun destroy() {
        mapView?.mapWindow?.map?.removeInputListener(inputListener)
        mapView?.mapWindow?.map?.removeCameraListener(cameraListener)
        markersMap.clear()
        mapView     = null
        markersCollection = null
        routesCollection  = null
        userLocationLayer = null
    }

    // ── API publique appelée depuis Compose ───────────────────────────────────────────

    fun setLocationEnabled(enabled: Boolean) {
        userLocationLayer?.isVisible           = enabled
        userLocationLayer?.isHeadingModeActive = enabled
    }

    fun centerOn(lat: Double, lng: Double, zoom: Float = 15f) {
        mapView?.mapWindow?.map?.move(
            CameraPosition(Point(lat, lng), zoom, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 0.8f),
            null
        )
    }

    fun recenter(userLat: Double?, userLng: Double?) {
        val target = if (userLat != null && userLng != null)
            Point(userLat, userLng) else YAOUNDE_CENTER
        val zoom = if (userLat != null) 15f else DEFAULT_ZOOM
        mapView?.mapWindow?.map?.move(
            CameraPosition(target, zoom, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 0.5f),
            null
        )
    }

    fun setSelectedPoint(point: DistributorPoint?) {
        currentSelectedPoint = point
        if (point != null) {
            mapView?.mapWindow
                ?.worldToScreen(Point(point.latitude, point.longitude))
                ?.let { onMarkerScreenPosition?.invoke(it.x, it.y) }
        }
        // Mettre à jour les scales
        markersMap.values.forEach { entry ->
            if (!entry.placemark.isValid) return@forEach
            val isSelected = entry.point.id == point?.id
            entry.composite.setIconStyle("pin", IconStyle().apply {
                anchor = PointF(0.5f, 1.0f)
                scale  = if (isSelected) 0.75f else 0.55f
                zIndex = if (isSelected) 100f  else 0f
            })
        }
    }

    fun updateRoute(routePoints: List<Point>) {
        val rc = routesCollection ?: return
        rc.clear()
        if (routePoints.size >= 2) {
            rc.addPolyline(Polyline(routePoints)).apply {
                strokeWidth = 7f
                setStrokeColor(0xFF2563EB.toInt())
                outlineWidth = 3f
                setOutlineColor(0xFFFFFFFF.toInt())
            }
        }
    }

    fun syncMarkers(points: List<DistributorPoint>) {
        val mc = markersCollection ?: return

        // Ajout des nouveaux
        points.forEach { point ->
            val existing = markersMap[point.id]
            if (existing == null || !existing.placemark.isValid) {
                if (existing != null) markersMap.remove(point.id)
                createMarker(mc, point)
            }
        }

        // Purge des supprimés
        val currentIds = points.map { it.id }.toSet()
        val iterator   = markersMap.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key !in currentIds) {
                val e = entry.value
                if (e.placemark.isValid) {
                    e.placemark.removeTapListener(e.tapListenerRef)
                    mc.remove(e.placemark)
                }
                iterator.remove()
            }
        }
    }

    // ── Création d'un marqueur ────────────────────────────────────────────────────────

    private fun createMarker(collection: MapObjectCollection, point: DistributorPoint) {
        val pm = collection.addPlacemark().apply {
            geometry = Point(point.latitude, point.longitude)
            setText(point.name, TextStyle().apply {
                size         = 10f
                color        = 0xFF003761.toInt()
                outlineColor = 0xFFFFFFFF.toInt()
                outlineWidth = 2f
                placement    = TextStyle.Placement.TOP
                offset       = 5f
            })
        }

        val composite = pm.useCompositeIcon().apply {
            setIcon("pin", markerImageProvider, IconStyle().apply {
                anchor = PointF(0.5f, 1.0f)
                scale  = 0.55f
                zIndex = 0f
            })
            setIcon("point", circleImageProvider, IconStyle().apply {
                anchor = PointF(0.5f, 0.5f)
                flat   = true
                scale  = 0.05f
            })
        }

        // Le listener est un champ de MarkerEntry → référence forte dans markersMap
        val entry = MarkerEntry(point, pm, composite)
        pm.addTapListener(entry.tapListenerRef)
        markersMap[point.id] = entry
    }

    // ── MarkerEntry avec listener comme champ (référence forte garantie) ──────────────

    private inner class MarkerEntry(
        val point      : DistributorPoint,
        val placemark  : PlacemarkMapObject,
        val composite  : CompositeIcon
    ) {
        // Déclaré comme val dans l'inner class → référence forte tant que MarkerEntry vit
        // Utilise les callbacks via MapController (jamais capturé directement)
        val tapListenerRef = MapObjectTapListener { _, _ ->
            onPointClick?.invoke(point)
            mapView?.mapWindow
                ?.worldToScreen(Point(point.latitude, point.longitude))
                ?.let { onMarkerScreenPosition?.invoke(it.x, it.y) }
            true
        }
    }
}