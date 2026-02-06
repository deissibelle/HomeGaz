package com.homegaz.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Auth DTOs
@Serializable
data class RegisterEmailRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterPhoneRequest(
    val phone: String,
    @SerialName("country_code") val countryCode: String
)

@Serializable
data class SendOtpRequest(
    val email: String? = null,
    val phone: String? = null,
    @SerialName("country_code") val countryCode: String? = null
)

@Serializable
data class VerifyOtpRequest(
    val email: String? = null,
    val phone: String? = null,
    @SerialName("country_code") val countryCode: String? = null,
    val otp: String,
    val password: String? = null
)

@Serializable
data class LoginEmailRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginPhoneRequest(
    val phone: String,
    @SerialName("country_code") val countryCode: String,
    val otp: String
)

@Serializable
data class ResetPasswordRequest(
    val email: String? = null,
    val phone: String? = null,
    @SerialName("country_code") val countryCode: String? = null,
    val otp: String,
    @SerialName("new_password") val newPassword: String
)

@Serializable
data class RefreshTokenRequest(
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class AuthResponseDto(
    val user: UserDto,
    val tokens: TokensDto
)

@Serializable
data class UserDto(
    val id: String,
    val email: String? = null,
    val phone: String? = null,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("profile_picture") val profilePicture: String? = null,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("is_email_verified") val isEmailVerified: Boolean = false,
    @SerialName("is_phone_verified") val isPhoneVerified: Boolean = false
)

@Serializable
data class TokensDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Long
)

// Gas Point DTOs
@Serializable
data class GasPointDto(
    val id: String,
    val name: String,
    val brand: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val phone: String,
    @SerialName("opening_hours") val openingHours: String,
    @SerialName("is_open") val isOpen: Boolean,
    @SerialName("available_weights") val availableWeights: List<Int>,
    val rating: Float = 0f,
    @SerialName("reviews_count") val reviewsCount: Int = 0,
    @SerialName("price_per_kg") val pricePerKg: Double
)

@Serializable
data class NearbyGasPointsRequest(
    val latitude: Double,
    val longitude: Double,
    @SerialName("radius_km") val radiusKm: Int = 100
)

// Reservation DTOs
@Serializable
data class CreateReservationRequest(
    @SerialName("gas_point_id") val gasPointId: String,
    val brand: String,
    val weight: Int,
    val quantity: Int,
    @SerialName("delivery_mode") val deliveryMode: String,
    @SerialName("needs_consigne") val needsConsigne: Boolean,
    @SerialName("delivery_address") val deliveryAddress: String? = null,
    @SerialName("delivery_latitude") val deliveryLatitude: Double? = null,
    @SerialName("delivery_longitude") val deliveryLongitude: Double? = null,
    val notes: String? = null
)

@Serializable
data class ReservationDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("gas_point_id") val gasPointId: String,
    @SerialName("gas_point") val gasPoint: GasPointDto? = null,
    val brand: String,
    val weight: Int,
    val quantity: Int,
    @SerialName("delivery_mode") val deliveryMode: String,
    @SerialName("needs_consigne") val needsConsigne: Boolean,
    @SerialName("consigne_amount") val consigneAmount: Int,
    val subtotal: Int,
    @SerialName("delivery_fee") val deliveryFee: Int,
    val total: Int,
    @SerialName("delivery_address") val deliveryAddress: String? = null,
    @SerialName("delivery_latitude") val deliveryLatitude: Double? = null,
    @SerialName("delivery_longitude") val deliveryLongitude: Double? = null,
    val notes: String? = null,
    @SerialName("created_at") val createdAt: Long,
    val status: String
)

// Order DTOs
@Serializable
data class CreateOrderRequest(
    @SerialName("reservation_id") val reservationId: String,
    @SerialName("payment_method") val paymentMethod: String
)

@Serializable
data class OrderDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("reservation_id") val reservationId: String,
    val reservation: ReservationDto? = null,
    @SerialName("payment_method") val paymentMethod: String,
    val status: String,
    @SerialName("status_history") val statusHistory: List<OrderStatusHistoryDto> = emptyList(),
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
    @SerialName("estimated_delivery_time") val estimatedDeliveryTime: Long? = null,
    @SerialName("actual_delivery_time") val actualDeliveryTime: Long? = null,
    @SerialName("cancel_reason") val cancelReason: String? = null
)

@Serializable
data class OrderStatusHistoryDto(
    val status: String,
    val timestamp: Long,
    val note: String? = null
)

// Payment DTOs
@Serializable
data class InitiatePaymentRequest(
    @SerialName("order_id") val orderId: String,
    val amount: Int,
    val method: String,
    @SerialName("phone_number") val phoneNumber: String? = null
)

@Serializable
data class ConfirmPaymentRequest(
    @SerialName("payment_id") val paymentId: String,
    val pin: String
)

@Serializable
data class PaymentDto(
    val id: String,
    @SerialName("order_id") val orderId: String,
    val method: String,
    val amount: Int,
    @SerialName("phone_number") val phoneNumber: String? = null,
    val status: String,
    @SerialName("transaction_id") val transactionId: String? = null,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("completed_at") val completedAt: Long? = null,
    @SerialName("error_message") val errorMessage: String? = null
)

// Generic Response
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: String? = null
)
