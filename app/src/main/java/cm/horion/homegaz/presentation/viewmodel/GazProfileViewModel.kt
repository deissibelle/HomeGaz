package cm.horion.homegaz.presentation.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.horion.homegaz.domain.model.gazprofile.CameroonData
import cm.horion.homegaz.domain.model.gazprofile.GazProfile
import cm.horion.homegaz.domain.usecase.LoadGazProfileUseCase
import cm.horion.homegaz.domain.usecase.SaveGazProfileUseCase
import cm.horion.homegaz.presentation.state.GazProfileUiState
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class GazProfileViewModel(
    private val saveProfile : SaveGazProfileUseCase,
    private val loadProfile : LoadGazProfileUseCase,
    private val context     : Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(GazProfileUiState())
    val uiState: StateFlow<GazProfileUiState> = _uiState.asStateFlow()

    init {
        loadExistingProfile()
    }

    //  Chargement

    private fun loadExistingProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = loadProfile() ?: return@launch
            _uiState.update {
                it.copy(
                    capacityKg    = profile.capacityKg,
                    brand         = profile.brand,
                    usageLocation = profile.usageLocation,
                    region        = profile.region        ?: "",
                    ville         = profile.ville         ?: "",
                    quartier      = profile.quartier      ?: "",
                    lieuDit       = profile.lieuDit       ?: "",
                    latitude      = profile.latitude,
                    longitude     = profile.longitude,
                )
            }
        }
    }

    // Mise à jour des champs

    fun onCapacityChange(value: String)  = _uiState.update { it.copy(capacityKg    = value) }
    fun onBrandChange(value: String)     = _uiState.update { it.copy(brand         = value) }
    fun onLocationChange(value: String)  = _uiState.update { it.copy(usageLocation = value) }
    fun onRegionChange(value: String)    = _uiState.update { it.copy(region        = value, ville = "", quartier = "") }
    fun onVilleChange(value: String)     = _uiState.update { it.copy(ville         = value, quartier = "") }
    fun onQuartierChange(value: String)  = _uiState.update { it.copy(quartier      = value) }
    fun onLieuDitChange(value: String)   = _uiState.update { it.copy(lieuDit       = value) }

    // Détection GPS

    /**
     * Récupère la position GPS de l'utilisateur, puis géocode l'adresse
     * pour remplir automatiquement : usageLocation, région, ville.
     *
     * Requiert la permission ACCESS_FINE_LOCATION accordée avant l'appel.
     */
    @SuppressLint("MissingPermission")
    fun detectLocationFromGps() {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)

        // 1. Tenter la dernière position connue (rapide)
        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                geocodeAndFill(location.latitude, location.longitude)
            } else {
                // 2. Sinon demander une position fraîche
                val request = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY, 5_000L
                ).setMaxUpdates(1).build()

                fusedClient.requestLocationUpdates(
                    request,
                    object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            fusedClient.removeLocationUpdates(this)
                            result.lastLocation?.let { loc ->
                                geocodeAndFill(loc.latitude, loc.longitude)
                            }
                        }
                    },
                    Looper.getMainLooper()
                )
            }
        }
    }

    /**
     * Géocode les coordonnées et met à jour l'état avec
     * – usageLocation : adresse lisible complète
     * — region : région du Cameroun correspondante (si trouvée).
     * - Ville : ville correspondante (si trouvée)
     */
    private fun geocodeAndFill(lat: Double, lng: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                @Suppress("DEPRECATION")
                val geocoder   = Geocoder(context, Locale.FRENCH)
                val addresses  = geocoder.getFromLocation(lat, lng, 1)
                val address    = addresses?.firstOrNull()

                val locality     = address?.locality         ?: ""
                val subLocality  = address?.subLocality      ?: ""
                val adminArea    = address?.adminArea         ?: ""
                val thoroughfare = address?.thoroughfare      ?: ""
                val featureName  = address?.featureName       ?: ""

                // Construire une adresse lisible
                val readableAddress = buildString {
                    if (thoroughfare.isNotBlank()) append(thoroughfare)
                    if (subLocality.isNotBlank()) {
                        if (isNotEmpty()) append(", ")
                        append(subLocality)
                    }
                    if (locality.isNotBlank()) {
                        if (isNotEmpty()) append(", ")
                        append(locality)
                    }
                }.ifBlank { "$lat, $lng" }

                // Matcher la région Cameroun
                val matchedRegion = matchCameroonRegion(adminArea)

                // Matcher la ville dans nos données
                val matchedVille = matchCameroonCity(locality, matchedRegion)

                _uiState.update { current ->
                    current.copy(
                        latitude      = lat,
                        longitude     = lng,
                        usageLocation = readableAddress,
                        region        = matchedRegion.ifBlank { current.region },
                        ville         = matchedVille.ifBlank  { current.ville  },
                        quartier      = if (subLocality.isNotBlank() && matchedVille == "Yaoundé")
                            matchYaoundeQuartier(subLocality)
                        else current.quartier,
                        lieuDit       = if (featureName.isNotBlank() && featureName != locality)
                            featureName
                        else current.lieuDit
                    )
                }
            } catch (e: Exception) {
                // En cas d'échec du géocodage, on stocke quand même les coordonnées brutes
                _uiState.update { it.copy(
                    latitude      = lat,
                    longitude     = lng,
                    usageLocation = "$lat, $lng"
                ) }
            }
        }
    }

    /** Essaie de faire correspondre l'adminArea avec une région camerounaise connue */
    private fun matchCameroonRegion(adminArea: String): String {
        if (adminArea.isBlank()) return ""
        val normalized = adminArea.lowercase().trim()
        return when {
            normalized.contains("centre")        -> "Centre"
            normalized.contains("littoral")      -> "Littoral"
            normalized.contains("ouest")         -> "Ouest"
            normalized.contains("nord-ouest") ||
                    normalized.contains("northwest")     -> "Nord-Ouest"
            normalized.contains("sud-ouest") ||
                    normalized.contains("southwest")     -> "Sud-Ouest"
            normalized.contains("nord")          -> "Nord"
            normalized.contains("sud")           -> "Sud"
            normalized.contains("adamaoua")      -> "Adamaoua"
            normalized.contains("extrême") ||
                    normalized.contains("extreme") ||
                    normalized.contains("far north")     -> "Extrême-Nord"
            normalized.contains("est") ||
                    normalized.contains("east")          -> "Est"
            else                                 -> ""
        }
    }

    /** Essaie de faire correspondre la locality avec une ville connue dans la région */
    private fun matchCameroonCity(locality: String, region: String): String {
        if (locality.isBlank()) return ""
        val cities = CameroonData.getCitiesForRegion(region)
        return cities.firstOrNull { it.equals(locality, ignoreCase = true) }
            ?: cities.firstOrNull { locality.lowercase().contains(it.lowercase()) }
            ?: ""
    }

    /** Essaie de faire correspondre le subLocality avec un quartier de Yaoundé */
    private fun matchYaoundeQuartier(subLocality: String): String {
        val quarters = CameroonData.YAOUNDE_QUARTERS
        return quarters.firstOrNull { it.equals(subLocality, ignoreCase = true) }
            ?: quarters.firstOrNull { subLocality.lowercase().contains(it.lowercase()) }
            ?: ""
    }

    //Sauvegarde

    fun saveProfile(onSuccess: () -> Unit = {}) {
        val state = _uiState.value
        if (!state.isFormValid) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                saveProfile(
                    GazProfile(
                        capacityKg    = state.capacityKg,
                        brand         = state.brand,
                        usageLocation = state.usageLocation,
                        region        = state.region,
                        ville         = state.ville,
                        quartier      = state.quartier,
                        lieuDit       = state.lieuDit,
                        latitude      = state.latitude,
                        longitude     = state.longitude,
                    )
                )
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
                launch(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = "Erreur lors de la sauvegarde")
                }
            }
        }
    }
}