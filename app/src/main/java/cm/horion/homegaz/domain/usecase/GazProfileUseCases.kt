package cm.horion.homegaz.domain.usecase

import cm.horion.homegaz.domain.model.gazprofile.GazProfile
import cm.horion.homegaz.domain.repository.GazProfileRepository

class SaveGazProfileUseCase(private val repository: GazProfileRepository) {
    operator fun invoke(profile: GazProfile) = repository.save(profile)
}

class LoadGazProfileUseCase(private val repository: GazProfileRepository) {
    operator fun invoke(): GazProfile? = repository.load()
}