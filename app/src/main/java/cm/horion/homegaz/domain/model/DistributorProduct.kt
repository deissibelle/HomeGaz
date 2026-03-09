package cm.horion.homegaz.domain.model

data class DistributorProduct(
    val brand: String,
    val weight: String,
    val unitPrice: Int,
    val currency: String = "frs"
)