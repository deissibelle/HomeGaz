package cm.horion.homegaz.domain.usecase

import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.home.DistributorPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class GetDistributorPointsUseCase {

    operator fun invoke(): Flow<List<DistributorPoint>> = flow {
        emit(
            listOf(
                DistributorPoint(
                    id = "1", name = "SCTM Bastos",
                    latitude = 3.882, longitude = 11.514,
                    distributor = "SCTM", priceXaf = 6500,
                    stockAvailable = true,
                    weight = "12kg", logoRes = R.drawable.distributor_logo
                ),
                DistributorPoint(
                    id = "2", name = "Total Melen",
                    latitude = 3.861, longitude = 11.521,
                    distributor = "Total", priceXaf = 6800,
                    stockAvailable = true,
                    weight = "12kg", logoRes = R.drawable.distributor_logo
                ),
                DistributorPoint(
                    id = "3", name = "Tradex Centre",
                    latitude = 3.848, longitude = 11.502,
                    distributor = "Tradex", priceXaf = 6600,
                    stockAvailable = true,
                    weight = "12kg", logoRes = R.drawable.distributor_logo
                ),
                DistributorPoint(
                    id = "4", name = "SCTM Essos",
                    latitude = 3.879, longitude = 11.533,
                    distributor = "SCTM", priceXaf = 6500,
                    stockAvailable = true,
                    weight = "6kg", logoRes = R.drawable.distributor_logo
                ),
                DistributorPoint(
                    id = "5", name = "Total Bastos",
                    latitude = 3.889, longitude = 11.517,
                    distributor = "Total", priceXaf = 6900,
                    stockAvailable = true,
                    weight = "12kg", logoRes = R.drawable.distributor_logo
                ),
                DistributorPoint(
                    id = "6", name = "Tradex Nlongkak",
                    latitude = 3.873, longitude = 11.526,
                    distributor = "Tradex", priceXaf = 6600,
                    stockAvailable = true,
                    weight = "12kg", logoRes = R.drawable.distributor_logo
                ),
                DistributorPoint(
                    id = "7", name = "SCTM Mvog-Ada",
                    latitude = 3.857, longitude = 11.515,
                    distributor = "SCTM", priceXaf = 6500,
                    stockAvailable = true,
                    weight = "6kg", logoRes = R.drawable.distributor_logo
                ),
                DistributorPoint(
                    id = "8", name = "Total Odza",
                    latitude = 3.804, longitude = 11.549,
                    distributor = "Total", priceXaf = 7000,
                    stockAvailable = true,
                    weight = "12kg", logoRes = R.drawable.distributor_logo
                ),
                DistributorPoint(
                    id = "9", name = "Tradex Ekounou",
                    latitude = 3.845, longitude = 11.542,
                    distributor = "Tradex", priceXaf = 6700,
                    stockAvailable = true,
                    weight = "6kg", logoRes = R.drawable.distributor_logo
                )
            )
        )
    }
}