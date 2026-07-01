package cm.horion.homegaz.presentation.viewmodel

import android.app.Application
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cm.horion.homegaz.data.datasource.local.GazBottleLocal
import cm.horion.homegaz.domain.model.Endpoint
import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle
import cm.horion.homegaz.domain.model.consommateur.dto.GazType
import cm.horion.homegaz.domain.model.distributor.dto.Distributor
import cm.horion.homegaz.domain.usecase.ConsumerUseCase
import cm.horion.homegaz.presentation.state.ConsumerUiState
import cm.horion.homegaz.util.ApiClient.client
import cm.horion.homegaz.util.Constants.GAZ_URL
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import cm.horion.homegaz.domain.usecase.LoadGazProfileUseCase
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.*
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.runtime.Error
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.math.*

class ConsumerViewModel(
    application: Application,
    private val loadProfile : LoadGazProfileUseCase,
    private val gazBottleLocal: GazBottleLocal,
    private val consumerUseCase: ConsumerUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ConsumerUiState())
    val uiState: StateFlow<ConsumerUiState> = _uiState.asStateFlow()

    var isDataReady by mutableStateOf(false)
        private set

    val isLocationFetched: Boolean
        get() = _uiState.value.isLocationFetched

    private var drivingSession: DrivingSession? = null

    private var fetchJob: Job? = null

    private var lastFetchTime = 0L

    init {
        viewModelScope.launch {
            val bottles = fetchAndGetGazBottles()
            _uiState.update { it.copy(availableBottles = bottles) }
            isDataReady = true

            // 2. On charge le profil
            val profile = loadProfile.invoke()
            if (profile != null) {
                // On cherche la bouteille correspondante à celle du profil de l'utilisateur
                val userBottle = bottles.find { it.id == profile.gazBottle || it.uuid == profile.gazBottle }

                if (userBottle != null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            selectedDistributor = userBottle.company.name, // ex: "Total", "Tradex"...
                            selectedWeight = "${userBottle.gazSize.size} kg",   // Utilise la propriété de taille de votre enum GazSize
                            battleUuid = userBottle.uuid            // Met aussi à jour l'UUID pour filtrer
                        )
                    }
                }
            }
            //prepareGazBottles()

            val granted = hasLocationPermission()
            _uiState.update { it.copy(locationGranted = granted, isFirstLaunch = !granted) }

            if (granted) {
                // ── ÉTAPE 1 : lastLocation immédiat (peut être null ou vieux) ──
                val lastKnown = getLastKnownLocation()
                if (lastKnown != null) {
                    Log.d("Location", "lastLocation immédiat: ${lastKnown.latitude}, ${lastKnown.longitude}")
                    _uiState.update {
                        it.copy(
                            userLat           = lastKnown.latitude,
                            userLng           = lastKnown.longitude,
                            isLocationFetched = true,
                            isRefiningLocation = true,
                        )
                    }
                    // ── ÉTAPE 2 : fetch immédiat avec lastLocation ──
                    fetch()
                } else {
                    // Pas de lastLocation → on débloque quand même l'UI
                    _uiState.update {
                        it.copy(
                            isLocationFetched = true,
                            isRefiningLocation = true,
                        )
                    }
                    fetch()
                }

                // ── ÉTAPE 3 : affine en arrière-plan sans bloquer l'UI ──
                viewModelScope.launch {
                    val precise = kotlinx.coroutines.withTimeoutOrNull(10_000L) {
                        getCurrentLocation()
                    }
                    if (precise != null && precise.accuracy <= 100f) {
                        val oldLat = _uiState.value.userLat
                        val oldLng = _uiState.value.userLng
                        val dist = if (oldLat != null && oldLng != null)
                            haversineKm(oldLat, oldLng, precise.latitude, precise.longitude) * 1000
                        else 999.0

                        // Re-fetch seulement si position significativement différente
                        if (dist > 200.0) {
                            Log.d("Location", "Position affinée: ${precise.latitude}, ${precise.longitude}")
                            _uiState.update {
                                it.copy(userLat = precise.latitude, userLng = precise.longitude)
                            }
                            fetch()
                        }
                    }
                    _uiState.update { it.copy(isRefiningLocation = false) }
                }

            } else {
                _uiState.update { it.copy(isLocationFetched = true) }
                fetch()
            }
        }
    }

    // ── lastLocation : instantané, peut être null ─────────────────────────────────
    @SuppressLint("MissingPermission")
    private suspend fun getLastKnownLocation(): android.location.Location? =
        kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            val fusedClient = LocationServices.getFusedLocationProviderClient(getApplication<Application>())
            fusedClient.lastLocation
                .addOnSuccessListener { location ->
                    if (continuation.isActive) continuation.resume(location) {}
                }
                .addOnFailureListener {
                    if (continuation.isActive) continuation.resume(null) {}
                }
        }



    // ── Copie ici ta fonction suspendue getCurrentLocation() ──
    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): android.location.Location? =
        kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            val fusedClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(getApplication<Application>())
            var locationCallback: com.google.android.gms.location.LocationCallback? = null

            fusedClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    if (continuation.isActive) continuation.resume(location) {}
                    return@addOnSuccessListener
                }

                val request = com.google.android.gms.location.LocationRequest.Builder(
                    com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                    10_000L
                ).setMinUpdateDistanceMeters(50f).build()

                locationCallback = object : com.google.android.gms.location.LocationCallback() {
                    override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                        locationCallback?.let { fusedClient.removeLocationUpdates(it) }
                        if (continuation.isActive) continuation.resume(result.lastLocation) {}
                    }
                }

                fusedClient.requestLocationUpdates(request, locationCallback!!, android.os.Looper.getMainLooper())
                    .addOnFailureListener { if (continuation.isActive) continuation.resume(null) {} }
            }.addOnFailureListener { if (continuation.isActive) continuation.resume(null) {} }

            continuation.invokeOnCancellation {
                locationCallback?.let { fusedClient.removeLocationUpdates(it) }
            }
        }


    private fun prepareGazBottles() {
        viewModelScope.launch {
            val localBottles = gazBottleLocal.getGazBottles()
            if (!localBottles.isNullOrEmpty()) {
                _uiState.update { it.copy(availableBottles = localBottles) }
                isDataReady = true
            } else {
                try {
                    val response: HttpResponse = client.get("$GAZ_URL${Endpoint.GetGaz.path}") {
                        accept(ContentType.Application.Json)
                    }
                    if (response.status == HttpStatusCode.OK) {
                        val gaz = Json.decodeFromString<List<GazBottle>>(response.bodyAsText())
                        gazBottleLocal.saveGazBottles(gaz)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isDataReady = true
                }
            }
        }
    }

    private suspend fun fetchAndGetGazBottles(): List<GazBottle> {
        val localBottles = gazBottleLocal.getGazBottles()
        if (!localBottles.isNullOrEmpty()) {
            return localBottles
        } else {
            return try {
                val response: HttpResponse = client.get("$GAZ_URL${Endpoint.GetGaz.path}") {
                    accept(ContentType.Application.Json)
                }
                if (response.status == HttpStatusCode.OK) {
                    val gaz = Json.decodeFromString<List<GazBottle>>(response.bodyAsText())
                    gazBottleLocal.saveGazBottles(gaz)
                    gaz
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        val ctx = getApplication<Application>()
        return ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun onLocationPermissionResult(isGranted: Boolean) {
        _uiState.value = _uiState.value.copy(
            locationGranted = isGranted,
            locationDenied  = !isGranted,
            isFirstLaunch   = false
        )
        if (isGranted) fetch()
    }

    fun onLocationChanged(latitude: Double, longitude: Double, accuracy: Float = 0f) {

        // ✅ Rejette les positions avec précision > 100m (GPS froid/drift)
        if (accuracy > 100f) {
            Log.d("Location", "Position ignorée — précision insuffisante (${accuracy.toInt()}m)")
            return
        }

        val oldLat = _uiState.value.userLat
        val oldLng = _uiState.value.userLng

        if (oldLat != null && oldLng != null) {
            val distanceDeplacement = haversineKm(oldLat, oldLng, latitude, longitude) * 1000
            if (distanceDeplacement < 50.0) {
                Log.d("Location", "Déplacement négligeable (${distanceDeplacement.toInt()}m). Ignoré.")
                return
            }
        }

        val now = System.currentTimeMillis()
        val shouldFetch = (now - lastFetchTime) > 30_000L

        Log.d("Location", "Nouvelle position validée : $latitude, $longitude (précision: ${accuracy.toInt()}m)")

        _uiState.update {
            it.copy(
                userLat = latitude,
                userLng = longitude,
                locationGranted   = true,
                isLocationFetched = true,
                isFirstLaunch     = false
            )
        }

        if (shouldFetch) {
            lastFetchTime = now
            fetch()
        }
    }


    fun onDistanceChange(newDistance: String) {
        _uiState.value = _uiState.value.copy(selectedDistance = newDistance)
        fetch()
    }

    fun onDistributorChange(companyName: String) {
        _uiState.value = _uiState.value.copy(selectedDistributor = companyName)
        updateSelectedBottle(companyName = companyName)
    }

    fun onWeightChange(weightName: String) {
        _uiState.value = _uiState.value.copy(selectedWeight = weightName)
        updateSelectedBottle(weightName = weightName)
    }

    private fun updateSelectedBottle(
        companyName: String = _uiState.value.selectedDistributor,
        weightName: String  = _uiState.value.selectedWeight
    ) {
        val bottles = _uiState.value.availableBottles
        if (bottles.isEmpty()) return
        val matchingBottle = bottles.find { bottle ->
            bottle.company.name == companyName &&
                    "${bottle.gazSize.size} kg" == weightName &&
                    bottle.gazType == GazType.BUTANE
        } ?: bottles.find { it.company.name == companyName && it.gazType == GazType.BUTANE }
        matchingBottle?.let { bottle ->
            _uiState.value = _uiState.value.copy(battleUuid = bottle.uuid)
            fetch()
        }
    }

    fun onBattleUuidChange(uuid: String) {
        _uiState.value = _uiState.value.copy(battleUuid = uuid)
        fetch()
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun fetch() {
        val lat = _uiState.value.userLat
        val lng = _uiState.value.userLng
        if (lat == null || lng == null) {
            _uiState.value = _uiState.value.copy(error = "Position utilisateur indisponible")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val result = consumerUseCase.getDepotGaz(
                    lat, lng,
                    _uiState.value.selectedDistance,
                    _uiState.value.battleUuid
                )
                _uiState.value = _uiState.value.copy(
                    allPoints      = result,
                    filteredPoints = applyLocalFilters(result, _uiState.value.selectedDistributor, _uiState.value.selectedWeight),
                    isLoading      = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error     = e.localizedMessage ?: "Erreur lors de la récupération des dépôts"
                )
            }
        }
    }

    private fun applyLocalFilters(
        points: List<Distributor>,
        distributorFilter: String,
        weightFilter: String
    ): List<Distributor> {
        val lat = _uiState.value.userLat
        val lng = _uiState.value.userLng
        return points
            .map { p ->
                if (lat != null && lng != null)
                    p.copy(distance = haversineKm(lat, lng, p.address.location.latitude, p.address.location.longitude))
                else p
            }
            .filter { p ->
                distributorFilter == "Tous" || p.name.contains(distributorFilter, ignoreCase = true)
            }
            .sortedBy { it.distance }
    }

    fun onPointClick(point: Distributor) {
        drivingSession?.cancel()
        drivingSession = null
        _uiState.value = _uiState.value.copy(
            selectedPoint    = point,
            routePolyline    = emptyList(),
            routeBoundingBox = null
        )
    }

    fun onDismissPopup() {
        drivingSession?.cancel()
        drivingSession = null
        _uiState.value = _uiState.value.copy(
            selectedPoint    = null,
            routePolyline    = emptyList(),
            routeBoundingBox = null
        )
    }

    // ── CALCUL D'ITINÉRAIRE ───────────────────────────────────────────────────
    fun calculateRouteToPoint(destLat: Double, destLng: Double) {
        val startLat = _uiState.value.userLat ?: return
        val startLng = _uiState.value.userLng ?: return

        drivingSession?.cancel()
        drivingSession = null

        // On garde selectedPoint visible pendant le calcul
        // pour que l'utilisateur sache sur quel dépôt porte l'itinéraire
        _uiState.value = _uiState.value.copy(
            isLoading        = true,
            error            = null,
            routePolyline    = emptyList(),
            routeBoundingBox = null
        )

        val start = Point(startLat, startLng)
        val dest  = Point(destLat, destLng)

        drivingSession = DirectionsFactory.getInstance()
            .createDrivingRouter(DrivingRouterType.COMBINED)
            .requestRoutes(
                listOf(
                    RequestPoint(start, RequestPointType.WAYPOINT, null, null, null),
                    RequestPoint(dest,  RequestPointType.WAYPOINT, null, null, null)
                ),
                DrivingOptions().apply { routesCount = 1 },
                VehicleOptions(),
                object : DrivingSession.DrivingRouteListener {
                    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
                        if (routes.isEmpty()) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error     = "Aucun itinéraire trouvé"
                            )
                            return
                        }
                        val geometry = routes[0].geometry.points
                        _uiState.value = _uiState.value.copy(
                            // On ferme le sheet SEULEMENT quand la route est prête
                            selectedPoint    = null,
                            routePolyline    = geometry,
                            routeBoundingBox = computeBoundingBox(geometry),
                            isLoading        = false
                        )
                    }

                    override fun onDrivingRoutesError(error: Error) {
                        // Fallback : ligne droite si pas de réseau
                        _uiState.value = _uiState.value.copy(
                            selectedPoint    = null,
                            routePolyline    = listOf(start, dest),
                            routeBoundingBox = computeBoundingBox(listOf(start, dest)),
                            isLoading        = false,
                            error            = "Itinéraire approximatif (hors ligne)"
                        )
                    }
                }
            )
    }

    private fun computeBoundingBox(points: List<Point>): BoundingBox? {
        if (points.isEmpty()) return null
        return BoundingBox(
            Point(points.minOf { it.latitude },  points.minOf { it.longitude }),
            Point(points.maxOf { it.latitude },  points.maxOf { it.longitude })
        )
    }

    private fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r    = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a    = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    override fun onCleared() {
        super.onCleared()
        drivingSession?.cancel()
    }
}