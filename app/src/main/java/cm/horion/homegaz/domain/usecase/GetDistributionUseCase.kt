package cm.horion.homegaz.domain.usecase

import cm.horion.homegaz.domain.model.home.DistributionPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetDistributionPointsUseCase {

    operator fun invoke(): Flow<List<DistributionPoint>> = flow {
        emit(
            listOf(
                DistributionPoint("1","SCTM Bastos", latitude = 3.882, longitude = 11.514, distributor = "SCTM", priceXaf = 6500, stockAvailable = true),
                DistributionPoint("2","Total Melen", latitude = 3.861, longitude = 11.521, distributor = "Total", priceXaf = 6800, stockAvailable = true),
                DistributionPoint("3","Tradex Centre", latitude = 3.848, longitude = 11.502, distributor = "Tradex", priceXaf = 6600, stockAvailable = true),

                DistributionPoint("4","SCTM Essos", latitude = 3.879, longitude = 11.533, distributor = "SCTM", priceXaf = 6500, stockAvailable = true),

                DistributionPoint("5","Total Bastos", latitude = 3.889, longitude = 11.517, distributor = "Total", priceXaf = 6900, stockAvailable = false),

                DistributionPoint("6","Tradex Nlongkak", latitude = 3.873, longitude = 11.526, distributor = "Tradex", priceXaf = 6600, stockAvailable = true),

                DistributionPoint("7","SCTM Mvog-Ada", latitude = 3.857, longitude = 11.515, distributor = "SCTM", priceXaf = 6500, stockAvailable = true),

                DistributionPoint("8","Total Odza", latitude = 3.804, longitude = 11.549, distributor = "Total", priceXaf = 7000, stockAvailable = true),

                DistributionPoint("9","Tradex Ekounou", latitude = 3.845, longitude = 11.542, distributor = "Tradex", priceXaf = 6700, stockAvailable = false)
            )
        )
    }
}
