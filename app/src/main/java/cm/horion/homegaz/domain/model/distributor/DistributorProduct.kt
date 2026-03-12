package cm.horion.homegaz.domain.model.distributor

data class DistributorProduct(
    val brand: String,
    val weight: String,
    val unitPrice: Int,
    val currency: String = "frs"
)