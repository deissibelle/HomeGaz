package cm.horion.homegaz.domain.usecase

import cm.horion.homegaz.domain.model.home.DistributionPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetDistributionPointsUseCase {
    operator fun invoke(): Flow<List<DistributionPoint>> = flow {
        emit(
            listOf(
                DistributionPoint("1", "SCTM Bastos",    latitude = 3.882, longitude = 11.514, distributor = "SCTM",   priceXaf = 6500, stockAvailable = true),
                DistributionPoint("2", "Total Melen",    latitude = 3.861, longitude = 11.521, distributor = "Total",  priceXaf = 6800, stockAvailable = false),
                DistributionPoint("3", "Tradex Centre",  latitude = 3.848, longitude = 11.502, distributor = "Tradex", priceXaf = 6600, stockAvailable = true),
            )
        )
    }
}
