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



    private val ORANGE_PREFIXES = listOf("69", "65", "655", "656", "657", "658", "659")
    private val MTN_PREFIXES    = listOf("67", "68", "650", "651", "652", "653", "654")


    fun normalizePhone(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        return when {
            digits.startsWith("237") && digits.length == 12 -> digits.drop(3)
            else -> digits
        }
    }

    fun isPhoneValidForMethod(phone: String, method: PaymentMethod): Boolean {
        val normalized = normalizePhone(phone)
        if (normalized.length != 9) return false

        val prefix2 = normalized.take(2)
        val prefix3 = normalized.take(3)

        return when (method) {
            PaymentMethod.OM ->
                ORANGE_PREFIXES.any { normalized.startsWith(it) }
            PaymentMethod.MOMO ->
                MTN_PREFIXES.any { normalized.startsWith(it) }
        }
    }


    fun phoneErrorMessage(phone: String, method: PaymentMethod): String? {
        if (phone.isBlank()) return null
        val normalized = normalizePhone(phone)
        if (normalized.length != 9) return "Numéro invalide (9 chiffres requis)"
        return if (!isPhoneValidForMethod(phone, method)) {
            when (method) {
                PaymentMethod.OM -> "Ce numéro n'est pas un numéro Orange"
                PaymentMethod.MOMO -> "Ce numéro n'est pas un numéro MTN"
            }
        } else null
    }
}