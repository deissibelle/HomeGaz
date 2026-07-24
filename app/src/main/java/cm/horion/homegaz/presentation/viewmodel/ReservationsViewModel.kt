package cm.horion.homegaz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.horion.homegaz.data.datasource.remote.GazBottleLocal
import cm.horion.homegaz.domain.model.distributor.OrderSummary
import cm.horion.homegaz.domain.model.reservation.mockReservations
import cm.horion.homegaz.domain.model.reservation.toReservation
import cm.horion.homegaz.domain.usecase.DistributorUseCase
import cm.horion.homegaz.presentation.state.ReservationsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReservationsViewModel(
    private val gazBottleLocal: GazBottleLocal,
    private val payUseCase: DistributorUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationsUiState())
    val uiState: StateFlow<ReservationsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val localBottles = gazBottleLocal.getGazBottles()
            _uiState.update { it.copy(availableBottles = localBottles) }
            loadReservations()
        }
    }


    fun loadReservations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // 1. Appel de ton UseCase pour récupérer les données du serveur/BDD
                val orders = payUseCase.getAllOrder()

                _uiState.update {
                    it.copy(
                        orders = orders,
                        reservations = mockReservations, // 🎯 Si tu as besoin de convertir tes Orders en Reservations
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Erreur inconnue")
                }
            }
        }
    }

    fun detailGaz(bottleUuid: String) {
        val selectedBottle = _uiState.value.availableBottles?.find { it.uuid == bottleUuid }
        _uiState.update { it.copy(gaz = selectedBottle) }
    }

    fun getCompany(uuid: String): String {
        val bottle = _uiState.value.availableBottles?.find { it.uuid == uuid }
        // Retourne le nom de la compagnie si trouvé, sinon une valeur par défaut
        return bottle?.company?.name ?: "Inconnu"
    }

    fun getWeight(uuid: String): String {
        val bottle = _uiState.value.availableBottles?.find { it.uuid == uuid }
        // Retourne le poids (ex: "12,5kg") si trouvé, sinon une chaîne vide
        return bottle?.gazSize?.size?.let { "${it}kg" } ?: ""
    }

}