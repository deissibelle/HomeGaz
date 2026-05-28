package cm.horion.homegaz.domain.usecase

import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.domain.model.distributor.dto.Distributor
import cm.horion.homegaz.domain.model.response.Response
import cm.horion.homegaz.domain.repository.ConsumerRepository
import org.koin.core.component.KoinComponent

class ConsumerUseCase (
    private val repository: ConsumerRepository
) : KoinComponent {

    suspend fun getGaz() : List<GazBottle> {
        return repository.getGaz()
    }

    suspend fun saveProfil(
        latitude : Double,
        longitude: Double,
        paymentMethod : PaymentMethod,
        quarter : String,
        city : String,
        region : String,
        lieuDit : String ,
        gazBottle: String
    ) : Response {
        return repository.saveProfil(latitude,longitude,paymentMethod,quarter,city,region,lieuDit,gazBottle)
    }

    suspend fun updateProfile(
        latitude : Double,
        longitude: Double,
        paymentMethod : PaymentMethod,
        quarter : String,
        city : String,
        region : String,
        lieuDit : String ,
        gazBottle: String
    ) : Response {
        return repository.updateProfile(latitude,longitude,paymentMethod,quarter,city,region,lieuDit,gazBottle)
    }

    suspend fun getProfile() : Response {
        return repository.getProfile()
    }

    suspend fun getDepotGaz(latitude : String, longitude: String,radiusKm: String,battleUuid: String) : List<Distributor> {
        return repository.getDepotGaz(latitude,longitude,radiusKm,battleUuid)
    }

}