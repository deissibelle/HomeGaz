package cm.horion.homegaz.domain.usecase

import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle

class DistributorUseCase {

    fun getGazBottle() : GazBottle {
        return GazBottle(
            id = "",
            uuid = "",
            company = TODO(),
            gazSize = TODO(),
            gazType = TODO()
        )
    }
}