package cm.horion.homegaz.data.repository

import android.util.Log
import cm.horion.homegaz.data.datasource.remote.ConsumerService
import cm.horion.homegaz.domain.model.consommateur.dto.Address
import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle
import cm.horion.homegaz.domain.model.consommateur.dto.GeoLocation
import cm.horion.homegaz.domain.model.consommateur.request.ProfileRequest
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.domain.model.distributor.dto.Distributor
import cm.horion.homegaz.domain.model.response.Response
import cm.horion.homegaz.domain.repository.ConsumerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConsumerRepositoryImpl(
    val consumerService: ConsumerService
) : ConsumerRepository {

    override suspend fun getGaz(): List<GazBottle> {
        return withContext(Dispatchers.IO) {
            try {
                consumerService.getGaz()
            } catch (e : Exception) {
                emptyList()
            }
        }
    }

    override suspend fun saveProfil(
        latitude : Double,
        longitude: Double,
        paymentMethod : PaymentMethod,
        quarter : String,
        city : String,
        region : String,
        lieuDit : String ,
        gazBottle: String
    ): Response {
        return withContext(Dispatchers.IO) {
            try {
                val request = ProfileRequest(
                    address = Address(
                        location = GeoLocation.fromLatLng(
                            latitude = latitude,
                            longitude = longitude
                        ),
                        quarter = quarter,
                        city = city,
                        region = region,
                        country = "Cameroun",
                        lieuDit = lieuDit
                    ),
                    paymentMethod = PaymentMethod.OM,
                    gazBottle = gazBottle
                )
                consumerService.saveProfil(request)
            } catch (e : Exception) {
                Response(true, e.message.toString())
            }
        }
    }

    override suspend fun updateProfile(
        latitude : Double,
        longitude: Double,
        paymentMethod : PaymentMethod,
        quarter : String,
        city : String,
        region : String,
        lieuDit : String ,
        gazBottle: String
    ): Response {
        return withContext(Dispatchers.IO) {
            try {
                val request = ProfileRequest(
                    address = Address(
                        location = GeoLocation.fromLatLng(
                            latitude = latitude,
                            longitude = longitude
                        ),
                        quarter = quarter,
                        city = city,
                        region = region,
                        country = "Cameroun",
                        lieuDit = lieuDit
                    ),
                    paymentMethod = PaymentMethod.OM,
                    gazBottle = gazBottle
                )
                consumerService.updateProfile(request)
            } catch (e : Exception) {
                Response(true, e.message.toString())
            }
        }
    }

    override suspend fun getProfile(): Response {
        return withContext(Dispatchers.IO) {
            try {
                consumerService.getProfile()
            } catch (e : Exception) {
                Response(true, e.message.toString())
            }
        }
    }

    override suspend fun getDepotGaz(latitude: Double, longitude: Double, radiusKm: String, battleUuid: String): List<Distributor> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("Dist","entrer 3")
                val radiusInKm: Double = when (radiusKm) {
                    "100 mètre" -> 0.1
                    "500 mètre" -> 0.5
                    "1 km"      -> 1.0
                    "5 km"      -> 5.0
                    "10 km"     -> 10.0
                    else        -> 2.0 // 2 km par défaut
                }
                consumerService.getDepotGaz(latitude,longitude,radiusInKm.toString(),battleUuid)
            } catch (e : Exception) {
                emptyList()
            }
        }
    }
}