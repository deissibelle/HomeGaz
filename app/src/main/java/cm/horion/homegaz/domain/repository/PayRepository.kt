package cm.horion.homegaz.domain.repository

import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.domain.model.order.dto.DeliveryMode
import cm.horion.homegaz.domain.model.order.dto.OrderRequest
import cm.horion.homegaz.domain.model.payment.dto.PaymentRequest
import cm.horion.homegaz.domain.model.payment.dto.SessionsResponse
import cm.horion.homegaz.domain.model.response.Response

interface PayRepository {

    suspend fun payement(sessionsUuid: String, amount: Double, phoneNumber: String, method: PaymentMethod) : Response

    suspend fun saveOrder(distributorUuid: String, amount: Int,bottleUuid: String,quantity: Int,deliveryMode : DeliveryMode) : Response

    suspend fun getCvStatus(sessionUuid: String): SessionsResponse

}