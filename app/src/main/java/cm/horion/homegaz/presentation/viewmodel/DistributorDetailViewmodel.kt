package cm.horion.homegaz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import cm.horion.homegaz.domain.model.distributor.DeliveryOption
import cm.horion.homegaz.domain.model.home.DistributorPoint
import cm.horion.homegaz.domain.usecase.GetDistributorDetailUseCase
import cm.horion.homegaz.presentation.state.DistributorDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class DistributorDetailViewModel(
    private val getDistributorDetail: GetDistributorDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DistributorDetailUiState())
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
}