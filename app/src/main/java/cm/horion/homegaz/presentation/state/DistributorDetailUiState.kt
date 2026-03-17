package cm.horion.homegaz.presentation.state

import cm.horion.homegaz.domain.model.distributor.DeliveryOption
import cm.horion.homegaz.domain.model.distributor.DistributorProduct


data class DistributorDetailUiState(
    val product        : DistributorProduct? = null,
    val quantity       : Int                 = 1,
    val selectedOption : DeliveryOption      = DeliveryOption.LIVRAISON,
    val isLoading      : Boolean             = true,
    val error          : String?             = null
) {
    val total: Int get() = (product?.unitPrice ?: 0) * quantity
}