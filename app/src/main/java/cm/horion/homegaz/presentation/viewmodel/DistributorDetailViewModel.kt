package cm.horion.homegaz.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.horion.homegaz.data.datasource.local.GazBottleLocal
import cm.horion.homegaz.domain.model.consommateur.dto.Company
import cm.horion.homegaz.domain.model.distributor.DeliveryOption
import cm.horion.homegaz.domain.model.home.DistributorPoint
import cm.horion.homegaz.domain.usecase.DistributorUseCase
import cm.horion.homegaz.domain.usecase.GetDistributorDetailUseCase
import cm.horion.homegaz.presentation.state.ConsumerUiState
import cm.horion.homegaz.presentation.state.DistributorDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class DistributorDetailViewModel(
    //private val distributorlUseCase: DistributorUseCase,
    private val gazBottleLocal: GazBottleLocal,
    private val getDistributorDetail: GetDistributorDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DistributorDetailUiState())
    // Cette propriété publique reste le point d'écoute pour la vue et la navigation
    val uiState: StateFlow<DistributorDetailUiState> = _uiState.asStateFlow()


    fun loadPoint(point: DistributorPoint) {
        val product = getDistributorDetail(point)
        _uiState.update {
            it.copy(product = product, isLoading = false, error = null)
        }
    }


    fun onQuantityChange(newQuantity: Int) {
        if (newQuantity < 1) return
        _uiState.update { it.copy(quantity = newQuantity) }
    }

    fun onDeliveryOptionChange(option: DeliveryOption) {
        _uiState.update { it.copy(selectedOption = option) }
    }

    fun getGazBottle(uuid : String) {
        viewModelScope.launch {
            val gaz = gazBottleLocal.getGazBottleByUuid(uuid)
            _uiState.update {
                it.copy(
                    gaz = gaz
                )
            }
        }
    }
}