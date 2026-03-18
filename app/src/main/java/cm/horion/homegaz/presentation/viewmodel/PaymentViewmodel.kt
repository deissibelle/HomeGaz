package cm.horion.homegaz.presentation.viewmodel


import androidx.lifecycle.ViewModel
import cm.horion.homegaz.domain.model.distributor.DeliveryOption
import cm.horion.homegaz.domain.model.distributor.OrderSummary
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.domain.usecase.BuildOrderSummaryUseCase
import cm.horion.homegaz.presentation.state.PaymentUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class PaymentViewModel(
    private val buildOrderSummary: BuildOrderSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()


    fun loadOrder(
        brand          : String,
        weight         : String,
        quantity       : Int,
        deliveryOption : DeliveryOption,
        unitPrice      : Int
    ) {
        _uiState.update {
            it.copy(
                brand          = brand,
                weight         = weight,
                quantity       = quantity,
                deliveryOption = deliveryOption,
                unitPrice      = unitPrice,
                isLoading      = false
            )
        }
    }


    fun onPaymentMethodChange(method: PaymentMethod) {
        _uiState.update { it.copy(selectedMethod = method) }
    }

    fun onPhoneNumberChange(number: String) {
        _uiState.update { it.copy(phoneNumber = number) }
    }

    fun buildSummary(): OrderSummary? {
        val state = _uiState.value
        if (!state.isFormValid) return null
        return buildOrderSummary(
            brand          = state.brand,
            weight         = state.weight,
            quantity       = state.quantity,
            deliveryOption = state.deliveryOption,
            paymentMethod  = state.selectedMethod,
            phoneNumber    = state.phoneNumber,
            unitPrice      = state.unitPrice
        )
    }
}