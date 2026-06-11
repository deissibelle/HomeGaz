package cm.horion.homegaz.presentation.ui.components.home

import android.content.Context
import android.graphics.PointF
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.distributor.dto.Distributor
import com.yandex.mapkit.Animation
import com.yandex.mapkit.ConflictResolutionMode
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import android.graphics.Bitmap
import com.yandex.runtime.image.ImageProvider


private val YAOUNDE_FALLBACK = Point(3.848, 11.502)
private const val DEFAULT_ZOOM = 13f

class MapController(private val context: Context) {

    var onPointClick           : ((Distributor) -> Unit)?  = null
    var onDismissPopup         : (() -> Unit)?             = null
    var onMarkerScreenPosition : ((Float, Float) -> Unit)? = null

    var mapView                    : MapView?             = null
    private var markersCollection  : MapObjectCollection? = null
    private var routesCollection   : MapObjectCollection? = null
    private var userLocationLayer  : UserLocationLayer?   = null

    private val markerImageProvider : ImageProvider by lazy {
        ImageProvider.fromResource(context, R.drawable.marker)
    }
    private val circleImageProvider : ImageProvider by lazy {
        ImageProvider.fromResource(context, R.drawable.ic_circle)
    }
    private val userLocationImageProvider : ImageProvider by lazy {
        ImageProvider.fromResource(context, R.drawable.profil)
    }
    private val profileStrokeImageProvider : ImageProvider by lazy {
        ImageProvider.fromResource(context, R.drawable.profile_stroke)
    }

    private val markersMap = mutableMapOf<String, MarkerEntry>()

    private var hasActiveRoute : Boolean = false

    // Flag pour configurer l'icône utilisateur une seule fois
    // et éviter le clignotement causé par onObjectUpdated répété
    private var isUserLocationConfigured = false

    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            if (!hasActiveRoute) {
                onDismissPopup?.invoke()
            }
        }
        override fun onMapLongTap(map: Map, point: Point) = Unit
    }

    private val cameraListener = CameraListener { _, _, reason, _ ->
        if (reason == CameraUpdateReason.GESTURES) {
            if (!hasActiveRoute && currentSelectedPoint != null) {
                onDismissPopup?.invoke()
            }
        } else {
            currentSelectedPoint?.let { sp ->
                mapView?.mapWindow
                    ?.worldToScreen(Point(sp.address.location.latitude, sp.address.location.longitude))
                    ?.let { onMarkerScreenPosition?.invoke(it.x, it.y) }
            }
        }
    }

    private val userLocationObjectListener = object : UserLocationObjectListener {

        override fun onObjectAdded(userLocationView: UserLocationView) {
            // Configuration initiale, une seule fois
            configureUserLocationView(userLocationView)
            isUserLocationConfigured = true
        }

        override fun onObjectUpdated(userLocationView: UserLocationView, objectEvent: ObjectEvent) {
            // Ne reconfigurer que si l'objet a été retiré puis réajouté
            // Évite le clignotement causé par la reconstruction répétée du CompositeIcon
            if (!isUserLocationConfigured) {
                configureUserLocationView(userLocationView)
                isUserLocationConfigured = true
            }
            // Yandex MapKit gère la position automatiquement, rien d'autre à faire ici
        }

        override fun onObjectRemoved(userLocationView: UserLocationView) {
            // L'objet a été retiré, forcer la reconfiguration au prochain onObjectAdded
            isUserLocationConfigured = false
        }

        private fun configureUserLocationView(view: UserLocationView) {
            // 1. Remplacer le triangle Yandex par un bitmap transparent 1x1 px
            // isVisible = false ne suffit pas — Yandex le redessine à chaque heading update
            val emptyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            view.arrow.setIcon(
                ImageProvider.fromBitmap(emptyBitmap),
                IconStyle().apply {
                    scale  = 0f
                    zIndex = -1f
                }
            )
            view.arrow.isVisible = false

            // 2. Cercle de précision semi-transparent
            view.accuracyCircle.fillColor = 0x222563EB.toInt()

            val pinPlacemark = view.pin

            // 3. Label "Moi"
            pinPlacemark.setText("Moi", TextStyle().apply {
                size         = 12f
                color        = 0xFF003761.toInt()
                outlineColor = 0xFFFFFFFF.toInt()
                outlineWidth = 3f
                placement    = TextStyle.Placement.RIGHT
                offset       = 7f
            })

            // 4. Icône composite — construite une seule fois
            pinPlacemark.useCompositeIcon().apply {

                // Ombre au sol
                setIcon("shadow", circleImageProvider, IconStyle().apply {
                    anchor = PointF(0.5f, 0.5f)
                    scale  = 0.9f
                    zIndex = 0f
                })

                // Tige verticale bleue
                setIcon("stroke", profileStrokeImageProvider, IconStyle().apply {
                    anchor = PointF(0.5f, 1.0f)
                    scale  = 0.9f
                    zIndex = 1f
                })

                // Photo de profil ronde
                setIcon("avatar", userLocationImageProvider, IconStyle().apply {
                    anchor = PointF(0.5f, 1.45f)
                    scale  = 0.85f
                    zIndex = 2f
                })
            }
        }
    }

    private var currentSelectedPoint : Distributor? = null
    private var mapStyleJson          : String?      = null
    private var defaultCenter         : Point        = YAOUNDE_FALLBACK

    // -------------------------------------------------------------------------
    // Cycle de vie
    // -------------------------------------------------------------------------

    fun attachMapView(mv: MapView, initialLocation: android.location.Location? = null) {
        if (mapView != null) return
        mapView = mv

        defaultCenter = if (initialLocation != null)
            Point(initialLocation.latitude, initialLocation.longitude)
        else YAOUNDE_FALLBACK

        if (mapStyleJson == null) {
            mapStyleJson = context.resources
                .openRawResource(R.raw.map_style)
                .bufferedReader()
                .use { it.readText() }
        }

        val map = mv.mapWindow.map
        map.setMapStyle(mapStyleJson!!)
        map.mapObjects.conflictResolutionMode = ConflictResolutionMode.MAJOR
        map.addInputListener(inputListener)
        map.addCameraListener(cameraListener)

        markersCollection = map.mapObjects.addCollection()
        routesCollection  = map.mapObjects.addCollection()

        userLocationLayer = MapKitFactory.getInstance()
            .createUserLocationLayer(mv.mapWindow).apply {
                isVisible           = false
                isHeadingModeActive = false
                setObjectListener(userLocationObjectListener)
            }

        map.move(
            CameraPosition(defaultCenter, DEFAULT_ZOOM, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 0f),
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
        mapView                  = null
        markersCollection        = null
        routesCollection         = null
        userLocationLayer        = null
        hasActiveRoute           = false
        isUserLocationConfigured = false  // Reset du flag au destroy
    }

    fun setLocationEnabled(enabled: Boolean) {
        if (enabled) {
            userLocationLayer?.isVisible           = true
            userLocationLayer?.isHeadingModeActive = false  // Jamais true — sinon la caméra suit la rotation
            userLocationLayer?.isAutoZoomEnabled   = false  // Désactive le zoom automatique
        } else {
            userLocationLayer?.isVisible           = false
            userLocationLayer?.isHeadingModeActive = false
            userLocationLayer?.isAutoZoomEnabled   = false
            isUserLocationConfigured               = false
        }
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

    fun centerOnRadius(latitude: Double, longitude: Double, radiusInMeters: Float) {
        val map = mapView?.mapWindow?.map ?: return
        val geometry = Geometry.fromCircle(Circle(Point(latitude, longitude), radiusInMeters))
        val cameraPosition = map.cameraPosition(geometry)
        map.move(
            CameraPosition(cameraPosition.target, cameraPosition.zoom - 0.3f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1f),
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
        val mv = mapView          ?: return
        rc.clear()

        if (routePoints.size >= 2) {
            hasActiveRoute = true

            val polyline = Polyline(routePoints)

            rc.addPolyline(polyline).apply {
                strokeWidth = 7f
                setStrokeColor(0xFF003761.toInt())
                outlineWidth = 3f
                setOutlineColor(0xFFFFFFFF.toInt())
            }

            rc.addPlacemark().apply {
                geometry = routePoints.first()
                setIcon(
                    ImageProvider.fromResource(context, R.drawable.ic_circle),
                    IconStyle().apply {
                        anchor = PointF(0.5f, 0.5f)
                        scale  = 0.15f
                        zIndex = 10f
                    }
                )
            }

            val geometry = Geometry.fromPolyline(polyline)
            val position = mv.mapWindow.map.cameraPosition(geometry, null, null, null)
            mv.mapWindow.map.move(
                CameraPosition(position.target, position.zoom - 0.5f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        } else {
            hasActiveRoute = false
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

    fun updateUserLocation(location: android.location.Location) {
        defaultCenter = Point(location.latitude, location.longitude)
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

    private inner class MarkerEntry(
        val point     : Distributor,
        val placemark : PlacemarkMapObject,
        val composite : CompositeIcon
    ) {
        val tapListenerRef = MapObjectTapListener { _, _ ->
            if (hasActiveRoute) {
                hasActiveRoute = false
                routesCollection?.clear()
                onDismissPopup?.invoke()
            }
            onPointClick?.invoke(point)
            mapView?.mapWindow
                ?.worldToScreen(Point(point.address.location.latitude, point.address.location.longitude))
                ?.let { onMarkerScreenPosition?.invoke(it.x, it.y) }
            true
        }
    }
}