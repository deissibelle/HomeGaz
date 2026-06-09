package cm.horion.homegaz.domain.usecase

import cm.horion.homegaz.domain.model.consommateur.dto.Address
import cm.horion.homegaz.domain.model.consommateur.dto.GeoLocation
import cm.horion.homegaz.domain.model.consommateur.dto.Profile
import cm.horion.homegaz.domain.model.consommateur.request.ProfileRequest
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.domain.model.gazprofile.GazProfile
import cm.horion.homegaz.domain.repository.GazProfileRepository



class LoadGazProfileUseCase(private val repository: GazProfileRepository) {
    operator fun invoke(): Profile? = repository.load()

    fun save(profil : Profile) {
        repository.save(profil)
    }

}