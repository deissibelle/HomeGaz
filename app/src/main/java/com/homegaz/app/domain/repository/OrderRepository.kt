package com.homegaz.app.domain.repository

import com.homegaz.app.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    suspend fun createReservation(reservation: Reservation): Result<Reservation>
    suspend fun updateReservation(reservation: Reservation): Result<Reservation>
    suspend fun getReservationById(id: String): Result<Reservation>
    suspend fun deleteReservation(id: String): Result<Unit>
    suspend fun calculateTotal(reservation: Reservation): Int
    suspend fun calculateDeliveryFee(distanceKm: Double): Int
}

interface OrderRepository {
    suspend fun createOrder(reservation: Reservation, paymentMethod: PaymentMethod): Result<Order>
    suspend fun getOrderById(id: String): Result<Order>
    suspend fun getUserOrders(): Result<List<Order>>
    suspend fun cancelOrder(orderId: String, reason: String): Result<Order>
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus, note: String? = null): Result<Order>
    fun observeOrder(orderId: String): Flow<Order?>
    fun observeUserOrders(): Flow<List<Order>>
}

interface PaymentRepository {
    // Initiation du paiement
    suspend fun initiatePayment(
        orderId: String,
        amount: Int,
        method: PaymentMethod,
        phoneNumber: String? = null
    ): Result<Payment>
    
    // Confirmation du paiement
    suspend fun confirmPayment(paymentId: String, pin: String): Result<Payment>
    
    // Vérification du statut
    suspend fun getPaymentStatus(paymentId: String): Result<Payment>
    
    // Orange Money
    suspend fun processOrangeMoneyPayment(
        phoneNumber: String,
        amount: Int,
        orderId: String
    ): Result<Payment>
    
    // MTN Mobile Money
    suspend fun processMtnMomoPayment(
        phoneNumber: String,
        amount: Int,
        orderId: String
    ): Result<Payment>
    
    // Webhook pour les confirmations
    suspend fun handlePaymentWebhook(paymentId: String, status: PaymentStatus, transactionId: String?)
    
    // Observer le paiement
    fun observePayment(paymentId: String): Flow<Payment?>
}
