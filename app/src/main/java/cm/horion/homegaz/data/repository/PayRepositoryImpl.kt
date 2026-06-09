package cm.horion.homegaz.data.repository

import cm.horion.homegaz.data.datasource.remote.PayService
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.domain.model.order.dto.DeliveryMode
import cm.horion.homegaz.domain.model.order.dto.GazItem
import cm.horion.homegaz.domain.model.order.dto.OrderRequest
import cm.horion.homegaz.domain.model.payment.dto.PaymentRequest
import cm.horion.homegaz.domain.model.payment.dto.ServiceType
import cm.horion.homegaz.domain.model.payment.dto.SessionsResponse
import cm.horion.homegaz.domain.model.response.Response
import cm.horion.homegaz.domain.repository.PayRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PayRepositoryImpl(
    private val payService: PayService
) : PayRepository {

    override suspend fun payement(sessionsUuid: String, amount: Double, phoneNumber: String, method: PaymentMethod): Response {
        return withContext(Dispatchers.IO) {
            try {
                val pay = PaymentRequest(
                    userUuid = "",
                    sessionsUuid = sessionsUuid,
                    serviceType = ServiceType.GAZ,
                    amount = amount,
                    phoneNumber = phoneNumber,
                    method = method
                )
                payService.payement(pay)
            } catch (e: Exception ) {
                Response(false, e.message.toString())
            }
        }
    }

    override suspend fun saveOrder(distributorUuid: String, amount: Int,bottleUuid: String,quantity: Int,deliveryMode : DeliveryMode): Response {
        return withContext(Dispatchers.IO) {
            val order = OrderRequest(
                distributorUuid = distributorUuid,
                amount = amount,
                deliveryMode = deliveryMode,
                gaz = listOf(GazItem(
                    bottleUuid = bottleUuid,
                    quantity = quantity
                ))
            )
            try {
                payService.saveOrder(order)
            } catch (e: Exception){
                Response(false, e.message.toString())
            }
        }
    }

    override suspend fun getCvStatus(sessionUuid: String): SessionsResponse {
        return withContext(Dispatchers.IO) {
            payService.getCvStatus(sessionUuid)
        }
    }

    override suspend fun startTrackingPayment(paymentId: String) {
        return withContext(Dispatchers.IO) {
            try {
                payService.startTrackingPayment(paymentId)
            } catch (e : Exception) {
                println(e.message)
            }
        }
    }

}