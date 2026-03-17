package cm.horion.homegaz.domain.model.distributor

data class DistributorProduct(
    val pointName : String,
    val logoRes   : Int?,
    val brand: String,
    val weight: String,
    val unitPrice: Int,
    val currency: String = "frs"
)