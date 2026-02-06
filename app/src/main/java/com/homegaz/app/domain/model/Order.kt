package com.homegaz.app.domain.model

data class Reservation(
    val id: String = "",
    val userId: String,
    val gasPointId: String,
    val gasPoint: GasPoint? = null,
    val brand: GasBrand,
    val weight: GasWeight,
    val quantity: Int,
    val deliveryMode: DeliveryMode,
    val needsConsigne: Boolean,
    val consigneAmount: Int,
    val subtotal: Int,
    val deliveryFee: Int,
    val total: Int,
    val deliveryAddress: String? = null,
    val deliveryLatitude: Double? = null,
    val deliveryLongitude: Double? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val status: ReservationStatus = ReservationStatus.DRAFT
)

enum class DeliveryMode(val displayName: String) {
    PICKUP("Retrait au point"),
    DELIVERY("Livraison à domicile")
}

enum class ReservationStatus {
    DRAFT,
    PENDING_PAYMENT,
    CONFIRMED
}

data class Order(
    val id: String,
    val userId: String,
    val reservationId: String,
    val reservation: Reservation? = null,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus,
    val statusHistory: List<OrderStatusHistory> = emptyList(),
    val createdAt: Long,
    val updatedAt: Long,
    val estimatedDeliveryTime: Long? = null,
    val actualDeliveryTime: Long? = null,
    val cancelReason: String? = null
)

enum class OrderStatus(val displayName: String) {
    PENDING("En attente de paiement"),
    PAID("Payé, en préparation"),
    PROCESSING("En préparation"),
    READY("Prêt pour retrait"),
    IN_DELIVERY("En livraison"),
    DELIVERED("Livré"),
    CANCELLED("Annulé"),
    REFUNDED("Remboursé")
}

data class OrderStatusHistory(
    val status: OrderStatus,
    val timestamp: Long,
    val note: String? = null
)

enum class PaymentMethod(val displayName: String) {
    ORANGE_MONEY("Orange Money"),
    MTN_MOMO("MTN Mobile Money"),
    CASH("Espèces (paiement à la livraison)")
}

data class Payment(
    val id: String,
    val orderId: String,
    val method: PaymentMethod,
    val amount: Int,
    val phoneNumber: String? = null,
    val status: PaymentStatus,
    val transactionId: String? = null,
    val createdAt: Long,
    val completedAt: Long? = null,
    val errorMessage: String? = null
)

enum class PaymentStatus {
    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED,
    CANCELLED
}
