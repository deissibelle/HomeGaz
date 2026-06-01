package cm.horion.homegaz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.horion.homegaz.data.datasource.local.GazBottleLocal
import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle
import cm.horion.homegaz.domain.model.distributor.DeliveryOption
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.domain.model.distributor.dto.Distributor
import cm.horion.homegaz.domain.model.home.DistributorPoint
import cm.horion.homegaz.domain.usecase.DistributorUseCase
import cm.horion.homegaz.domain.usecase.GetDistributorDetailUseCase
import cm.horion.homegaz.presentation.state.DistributorDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DistributorDetailViewModel(
    private val gazBottleLocal: GazBottleLocal,
    private val payUseCase: DistributorUseCase,
    private val getDistributorDetail: GetDistributorDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DistributorDetailUiState())
    // Point d'écoute pour la vue Compose
    val uiState: StateFlow<DistributorDetailUiState> = _uiState.asStateFlow()

    fun loadPoint(point: Distributor) {
        _uiState.update {
            it.copy(product = point, isLoading = false, error = null)
        }
    }


    fun loadAvailableBottles(battleUuid: String, stock: Map<String, Int>) {
        if (stock.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 1. Récupérer les détails de toutes les bouteilles en stock via la DB locale
                val bottlesDetails = stock.keys.mapNotNull { uuid ->
                    try {
                        // S'adapte à ta méthode locale (ex: findByUuid, getBottle, etc.)
                        gazBottleLocal.getGazBottleByUuid(uuid)
                    } catch (e: Exception) {
                        null
                    }
                }

                // 2. Déterminer la bouteille sélectionnée par défaut
                val defaultSelected = bottlesDetails.find { it.uuid == battleUuid }
                    ?: bottlesDetails.firstOrNull()

                _uiState.update {
                    it.copy(
                        availableBottles = bottlesDetails,
                        gaz = defaultSelected,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun initOrder() {
        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isOrderSuccess = false,
                    isProcessingPay = true,
                )
            }

            try {

                val response = payUseCase.saveOrder(
                    distributorUuid = currentState.product?.enterpriseUuid!!,
                    amount = currentState.total,
                    bottleUuid = currentState.gaz?.uuid!!,
                    quantity = currentState.quantity,
                    deliveryOption = currentState.selectedOption
                )

                if(response.success) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isOrderSuccess = true,
                            sessionsUuid = response.message
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isOrderSuccess = false,
                            isProcessingPay = false,
                            error = response.message
                        )
                    }
                }

            } catch (e : Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }

        }
    }

    fun initPayment() {
        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isPaymentSuccessLancer = false,
                    isPaymentSuccess = false,
                )
            }

            try {

                val response = payUseCase.payement(
                    sessionsUuid = currentState.sessionsUuid,
                    amount = currentState.total.toDouble(),
                    phoneNumber = currentState.phoneNumber,
                    method = currentState.selectedMethod
                )

                if(response.success) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isPaymentSuccessLancer = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isPaymentSuccessLancer = false,
                            isProcessingPay = false,
                            error = response.message
                        )
                    }
                }

            } catch (e : Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isProcessingPay = false,
                        error = e.message
                    )
                }
            }

        }
    }


    fun onBottleSelected(gazBottle: GazBottle) = _uiState.update { it.copy(gaz = gazBottle) }
    fun onQuantityChange(newQuantity: Int) = _uiState.update { it.copy(quantity = newQuantity) }
    fun onDeliveryOptionChange(option: DeliveryOption) = _uiState.update { it.copy(selectedOption = option) }

    // ── Actions Écran Paiement ──
    fun onPaymentMethodChange(method: PaymentMethod) = _uiState.update { it.copy(selectedMethod = method) }
    fun onPhoneNumberChange(phone: String) = _uiState.update { it.copy(phoneNumber = phone) }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun triggerPayment() {
        val currentState = _uiState.value
        if (!currentState.isFormValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingPay = true) }
            try {
                // Lance ton API de paiement (ex: Orange Money / MTN MoMo polling)
                // paymentUseCase.initiate(phone = currentState.phoneNumber, amount = currentState.total)
            } catch (e: Exception) {
                _uiState.update { it.copy(isProcessingPay = false, error = e.message) }
            }
        }
    }

}