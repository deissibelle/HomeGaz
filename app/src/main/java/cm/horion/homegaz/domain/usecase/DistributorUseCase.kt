package cm.horion.homegaz.domain.usecase

import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle
import cm.horion.homegaz.domain.model.distributor.DeliveryOption
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.domain.model.order.dto.DeliveryMode
import cm.horion.homegaz.domain.model.payment.dto.SessionsResponse
import cm.horion.homegaz.domain.model.response.Response
import cm.horion.homegaz.domain.repository.PayRepository
import org.koin.core.component.KoinComponent

class DistributorUseCase(
    private val payRepository: PayRepository
) : KoinComponent {

//    fun getGazBottle() : GazBottle {
//        return GazBottle(
//            id = "",
//            uuid = "",
//            company = TODO(),
//            gazSize = TODO(),
//            gazType = TODO()
//        )
//    }

    suspend fun saveOrder(distributorUuid: String, amount: Int,bottleUuid: String,quantity: Int,deliveryOption: DeliveryOption) : Response {
        var deliveryMode = if (deliveryOption == DeliveryOption.LIVRAISON) {
            DeliveryMode.DELIVERY
        } else {
            DeliveryMode.PICKUP
        }
        return payRepository.saveOrder(distributorUuid,amount,bottleUuid,quantity,deliveryMode)
    }

    suspend fun payement(sessionsUuid: String, amount: Double, phoneNumber: String, method: PaymentMethod) : Response {
        return payRepository.payement(sessionsUuid,amount,phoneNumber,method)
    }

    suspend fun getCvStatus(sessionUuid: String): SessionsResponse {
        return payRepository.getCvStatus(sessionUuid)
    }

}