package cm.horion.homegaz.presentation.ui.components.home

import android.content.Context
import android.graphics.PointF
import android.location.Location
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.distributor.dto.Distributor
import cm.horion.homegaz.domain.model.home.DistributorPoint
import com.yandex.mapkit.Animation
import com.yandex.mapkit.ConflictResolutionMode
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Geometry

private val YAOUNDE_FALLBACK = Point(3.848, 11.502)
private const val DEFAULT_ZOOM = 13f

class MapController(private val context: Context) {

    // ── Callbacks vers le monde Compose ──
    var onPointClick           : ((Distributor) -> Unit)? = null
    var onDismissPopup         : (() -> Unit)?                 = null
    var onMarkerScreenPosition : ((Float, Float) -> Unit)?     = null

    // ── Références JNI stables ──
    var mapView            : MapView?             = null
    private var markersCollection  : MapObjectCollection? = null
    private var routesCollection   : MapObjectCollection? = null
    private var userLocationLayer  : UserLocationLayer?   = null

    private val markerImageProvider : ImageProvider by lazy {
        ImageProvider.fromResource(context, R.drawable.marker)
    }
    private val circleImageProvider : ImageProvider by lazy {
        ImageProvider.fromResource(context, R.drawable.ic_circle)
    }

    // 🚀 ICI : Charge ton icône de position utilisateur personnalisée
    // Remplace R.drawable.ic_user_pin par le vrai nom de ton fichier ressource
    private val userLocationImageProvider : ImageProvider by lazy {
        ImageProvider.fromResource(context, R.drawable.profil)
    }

    private val markersMap = mutableMapOf<String, MarkerEntry>()

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
                    ?.worldToScreen(Point(sp.address.location.latitude, sp.address.location.longitude))
                    ?.let { onMarkerScreenPosition?.invoke(it.x, it.y) }
            }
        }
    }

    // 🚀 Écouteur d'ancrage visuel pour remplacer l'icône de géolocalisation par défaut de Yandex
    private val userLocationObjectListener = object : UserLocationObjectListener {
        override fun onObjectAdded(userLocationView: UserLocationView) {
            // On remplace la flèche par défaut par ton icône personnalisée
            userLocationView.pin.setIcon(userLocationImageProvider)
            userLocationView.arrow.setIcon(userLocationImageProvider)

            // On configure l'ombrage/cercle de précision autour si désiré (optionnel)
            userLocationView.accuracyCircle.fillColor = 0x222563EB.toInt() // Bleu transparent
        }

        override fun onObjectRemoved(userLocationView: UserLocationView) {
            // Rien à faire ici
        }

        // ✅ La bonne signature attendue par ta version du SDK Yandex
        override fun onObjectUpdated(userLocationView: UserLocationView, objectEvent: ObjectEvent) {
            // Rien à faire ici
        }
    }

    private var currentSelectedPoint : Distributor? = null
    private var mapStyleJson          : String?           = null
    private var defaultCenter         : Point  = YAOUNDE_FALLBACK

    // ── Cycle de vie ──────────────────────────────────────────────────────────────────

    fun attachMapView(mv: MapView, initialLocation: Location? = null) {
        if (mapView != null) return

        mapView = mv

        // 1. Définition du centre de départ
        if (initialLocation != null) {
            defaultCenter = Point(initialLocation.latitude, initialLocation.longitude)
        } else {
            defaultCenter = YAOUNDE_FALLBACK
        }

        if (mapStyleJson == null) {
            mapStyleJson = context.resources
                .openRawResource(R.raw.map_style)
                .bufferedReader()
                .use { it.readText() }
        }

        val map = mv.mapWindow.map
        map.setMapStyle(mapStyleJson!!)
        map.mapObjects.conflictResolutionMode = ConflictResolutionMode.MAJOR

        // 2. On applique les listeners avant le déplacement pour éviter les sauts graphiques
        map.addInputListener(inputListener)
        map.addCameraListener(cameraListener)

        markersCollection = map.mapObjects.addCollection()
        routesCollection  = map.mapObjects.addCollection()

        // 3. Configuration de la couche de géolocalisation native
        userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mv.mapWindow).apply {
            setObjectListener(userLocationObjectListener)
        }

        // 4. 🚀 DEPLACEMENT DIRECT : On force la caméra sur la position calculée
        map.move(
            CameraPosition(defaultCenter, DEFAULT_ZOOM, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 0f), // 0f pour un placement immédiat sans transition visible
            null
        )
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
        userLocationLayer?.setObjectListener(null)
        markersMap.clear()
        mapView     = null
        markersCollection = null
        routesCollection  = null
        userLocationLayer = null
    }

    // ── API publique ──────────────────────────────────────────────────────────────────

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
            Point(userLat, userLng) else defaultCenter
        val zoom = if (userLat != null) 15f else DEFAULT_ZOOM
        mapView?.mapWindow?.map?.move(
            CameraPosition(target, zoom, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 0.5f),
            null
        )
    }

    fun setSelectedPoint(point: Distributor?) {
        currentSelectedPoint = point
        if (point != null) {
            mapView?.mapWindow
                ?.worldToScreen(Point(point.address.location.latitude, point.address.location.longitude))
                ?.let { onMarkerScreenPosition?.invoke(it.x, it.y) }
        }
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

    fun syncMarkers(points: List<Distributor>) {
        val mc = markersCollection ?: return

        points.forEach { point ->
            val existing = markersMap[point.id]
            if (existing == null || !existing.placemark.isValid) {
                if (existing != null) markersMap.remove(point.id)
                createMarker(mc, point)
            }
        }

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

    private fun createMarker(collection: MapObjectCollection, point: Distributor) {
        val pm = collection.addPlacemark().apply {
            geometry = Point(point.address.location.latitude, point.address.location.longitude)
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

        val entry = MarkerEntry(point, pm, composite)
        pm.addTapListener(entry.tapListenerRef)
        markersMap[point.id] = entry
    }

    fun centerOnRadius(latitude: Double, longitude: Double, radiusInMeters: Float) {
        val map = mapView?.mapWindow?.map ?: return
        val centerPoint = Point(latitude, longitude)

        // 1. On crée le cercle virtuel
        val circle = Circle(centerPoint, radiusInMeters)
        val geometry = Geometry.fromCircle(circle)

        // 2. ✅ Surchargée simplifiée : On demande la position de la caméra uniquement basée sur la géométrie
        val cameraPosition = map.cameraPosition(geometry)

        // 3. On applique le déplacement de manière fluide avec le zoom calculé
        map.move(
            CameraPosition(
                cameraPosition.target,
                cameraPosition.zoom - 0.3f, // Petite marge pour ne pas coller le cercle aux bords de l'écran
                0f,
                0f
            ),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }


    fun updateUserLocation(location: android.location.Location) {
        defaultCenter = Point(location.latitude, location.longitude)
        mapView?.mapWindow?.map?.move(
            CameraPosition(defaultCenter, DEFAULT_ZOOM, 0f, 0f),
            com.yandex.mapkit.Animation(com.yandex.mapkit.Animation.Type.SMOOTH, 1f),
            null
        )
    }

    private inner class MarkerEntry(
        val point      : Distributor,
        val placemark  : PlacemarkMapObject,
        val composite  : CompositeIcon
    ) {
        val tapListenerRef = MapObjectTapListener { _, _ ->
            onPointClick?.invoke(point)
            mapView?.mapWindow
                ?.worldToScreen(Point(point.address.location.latitude, point.address.location.longitude))
                ?.let { onMarkerScreenPosition?.invoke(it.x, it.y) }
            true
        }
    }
}