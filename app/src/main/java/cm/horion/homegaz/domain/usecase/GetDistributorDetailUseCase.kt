package cm.horion.homegaz.domain.usecase

import cm.horion.homegaz.domain.model.distributor.DistributorProduct
import cm.horion.homegaz.domain.model.home.DistributorPoint


class GetDistributorDetailUseCase {

    operator fun invoke(point: DistributorPoint): DistributorProduct =
        DistributorProduct(
            pointName = point.name,
            logoRes   = point.logoRes,
            brand     = point.distributor,
            weight    = point.weight,
            unitPrice = point.priceXaf
        )
}