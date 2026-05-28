package cm.horion.homegaz.domain.usecase

import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.home.DistributorPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.cos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.sin
import kotlin.random.Random

class GetDistributorPointsUseCase {
    operator fun invoke(
        centerLat: Double = 3.848,
        centerLng: Double = 11.502,
        radiusKm : Double = 5.0
    ): Flow<List<DistributorPoint>> = flow {


        val templates = listOf(
            Triple("SCTM Bastos",      "SCTM",   "12.5kg") to 6500,
            Triple("Total Melen",      "Total",  "12.5kg") to 6800,
            Triple("Tradex Centre",    "Tradex", "12.5kg") to 6600,
            Triple("SCTM Essos",       "SCTM",   "6kg")    to 6500,
            Triple("Total Bastos",     "Total",  "12.5kg") to 6900,
            Triple("Tradex Nlongkak",  "Tradex", "12.5kg") to 6600,
            Triple("SCTM Mvog-Ada",    "SCTM",   "6kg")    to 6500,
            Triple("Total Odza",       "Total",  "28kg")   to 7000,
            Triple("Tradex Ekounou",   "Tradex", "6kg")    to 6700,
            Triple("Glocal Gaz Nord",  "Glocal Gaz", "12.5kg") to 6450,
            Triple("Glocal Gaz Sud",   "Glocal Gaz", "6kg")    to 6400,
            Triple("SCTM Biyem-Assi",  "SCTM",   "28kg")  to 6500,
        )

        val points = templates.mapIndexed { index, (info, price) ->
            val (namePair, _) = info to price
            val (name, distributor, weight) = namePair

            val (lat, lng) = randomPointAround(centerLat, centerLng, radiusKm)

            DistributorPoint(
                id             = (index + 1).toString(),
                name           = name,
                latitude       = lat,
                longitude      = lng,
                distributor    = distributor,
                priceXaf       = price,
                stockAvailable = true,
                weight         = weight,
                logoRes        = R.drawable.distributor_logo
            )
        }

        emit(points)
    }


    private fun randomPointAround(
        lat     : Double,
        lng     : Double,
        radiusKm: Double
    ): Pair<Double, Double> {
        val distance = Random.nextDouble(0.2, radiusKm)
        val bearing  = Random.nextDouble(0.0, 360.0)

        val R        = 6371.0
        val bearingR = Math.toRadians(bearing)
        val latR     = Math.toRadians(lat)
        val lngR     = Math.toRadians(lng)
        val d        = distance / R

        val newLatR = asin(
            sin(latR) * cos(d) +
                    cos(latR) * sin(d) * cos(bearingR)
        )
        val newLngR = lngR + atan2(
            sin(bearingR) * Math.sin(d) * cos(latR),
            cos(d) - sin(latR) * sin(newLatR)
        )

        return Math.toDegrees(newLatR) to Math.toDegrees(newLngR)
    }
}