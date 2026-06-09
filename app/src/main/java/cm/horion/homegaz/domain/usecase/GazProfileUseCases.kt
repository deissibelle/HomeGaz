package cm.horion.homegaz.domain.usecase

import cm.horion.homegaz.domain.model.consommateur.dto.Address
import cm.horion.homegaz.domain.model.consommateur.dto.GeoLocation
import cm.horion.homegaz.domain.model.consommateur.request.ProfileRequest
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.domain.model.gazprofile.GazProfile
import cm.horion.homegaz.domain.repository.GazProfileRepository



class LoadGazProfileUseCase(private val repository: GazProfileRepository) {
    operator fun invoke(): ProfileRequest? = repository.load()

    fun save(
        latitude : Double,
        longitude: Double,
        paymentMethod : PaymentMethod,
        quarter : String,
        city : String,
        region : String,
        lieuDit : String ,
        gazBottle: String
    ) {
        val profile = ProfileRequest(
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
        repository.save(profile)
    }

}