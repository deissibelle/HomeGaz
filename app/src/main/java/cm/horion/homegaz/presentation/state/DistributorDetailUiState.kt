package cm.horion.homegaz.presentation.state

import cm.horion.homegaz.domain.model.consommateur.dto.Company
import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle
import cm.horion.homegaz.domain.model.consommateur.dto.GazSize
import cm.horion.homegaz.domain.model.consommateur.dto.GazType
import cm.horion.homegaz.domain.model.distributor.DeliveryOption
import cm.horion.homegaz.domain.model.distributor.DistributorProduct
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.domain.model.distributor.dto.Distributor
import cm.horion.homegaz.domain.model.payment.dto.PaymentStatus
import cm.horion.homegaz.util.isPaymentMethodValid


data class DistributorDetailUiState(
    // ── Données du Dépôt et de la Bouteille ──
    val product          : Distributor?    = null,
    val gaz              : GazBottle?      = null,
    val availableBottles : List<GazBottle> = emptyList(),
    val isLoading        : Boolean         = false,
    val error            : String?         = null,

    // ── Configuration de la Commande (Détail) ──
    val quantity         : Int             = 1,
    val selectedOption   : DeliveryOption  = DeliveryOption.LIVRAISON,

    // ── Informations de Paiement (Payment) ──
    val selectedMethod   : PaymentMethod = PaymentMethod.OM,
    val isOrderSuccess   : Boolean         = false,
    val isPaymentSuccessLancer   : Boolean         = false,
    val isPaymentSuccess   : Boolean         = false,
    val phoneNumber      : String          = "",
    val sessionsUuid      : String          = "",
    val isProcessingPay  : Boolean         = false,
    val isPaySuccess     : PaymentStatus   = PaymentStatus.PENDING
) {
    val unitPrice   : Int get() = gaz?.gazSize?.price ?: 6500
    val total       : Int get() = unitPrice * quantity
    val isFormValid : Boolean get() {
        return phoneNumber.isNotBlank() && isPaymentMethodValid(phoneNumber, selectedMethod)
    }
}