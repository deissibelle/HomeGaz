package cm.horion.homegaz.domain.repository

import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle
import cm.horion.homegaz.domain.model.consommateur.request.ProfileRequest
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.domain.model.distributor.dto.Distributor
import cm.horion.homegaz.domain.model.response.Response

interface ConsumerRepository {

    suspend fun getGaz() : List<GazBottle>

    suspend fun saveProfil(
        latitude : Double,
        longitude: Double,
        paymentMethod : PaymentMethod,
        quarter : String,
        city : String,
        region : String,
        lieuDit : String ,
        gazBottle: String
    ) : Response

    suspend fun updateProfile(
        latitude : Double,
        longitude: Double,
        paymentMethod : PaymentMethod,
        quarter : String,
        city : String,
        region : String,
        lieuDit : String ,
        gazBottle: String
    ) : Response

    suspend fun getProfile() : Response

    suspend fun getDepotGaz(latitude : Double, longitude: Double,radiusKm: String,battleUuid: String) : List<Distributor>

}