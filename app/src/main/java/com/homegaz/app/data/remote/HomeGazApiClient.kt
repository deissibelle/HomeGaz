package com.homegaz.app.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.homegaz.app.data.remote.dto.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeGazApiClient @Inject constructor(
    private val httpClient: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://api.homegaz.cm" // À remplacer par votre URL
    }
    
    // Auth Endpoints
    suspend fun registerWithEmail(request: RegisterEmailRequest): ApiResponse<Unit> {
        return httpClient.post("$BASE_URL/api/auth/register/email") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun registerWithPhone(request: RegisterPhoneRequest): ApiResponse<Unit> {
        return httpClient.post("$BASE_URL/api/auth/register/phone") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun sendOtp(request: SendOtpRequest): ApiResponse<Unit> {
        return httpClient.post("$BASE_URL/api/auth/send-otp") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun verifyOtp(request: VerifyOtpRequest): ApiResponse<AuthResponseDto> {
        return httpClient.post("$BASE_URL/api/auth/verify-otp") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun loginWithEmail(request: LoginEmailRequest): ApiResponse<AuthResponseDto> {
        return httpClient.post("$BASE_URL/api/auth/login/email") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun loginWithPhone(request: LoginPhoneRequest): ApiResponse<AuthResponseDto> {
        return httpClient.post("$BASE_URL/api/auth/login/phone") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun resetPassword(request: ResetPasswordRequest): ApiResponse<AuthResponseDto> {
        return httpClient.post("$BASE_URL/api/auth/reset-password") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun refreshToken(request: RefreshTokenRequest): ApiResponse<TokensDto> {
        return httpClient.post("$BASE_URL/api/auth/refresh-token") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun getCurrentUser(): ApiResponse<UserDto> {
        return httpClient.get("$BASE_URL/api/users/me").body()
    }
    
    // Gas Points Endpoints
    suspend fun getAllGasPoints(): ApiResponse<List<GasPointDto>> {
        return httpClient.get("$BASE_URL/api/gas-points").body()
    }
    
    suspend fun getNearbyGasPoints(request: NearbyGasPointsRequest): ApiResponse<List<GasPointDto>> {
        return httpClient.get("$BASE_URL/api/gas-points/nearby") {
            parameter("lat", request.latitude)
            parameter("lon", request.longitude)
            parameter("radius", request.radiusKm)
        }.body()
    }
    
    suspend fun getGasPointById(id: String): ApiResponse<GasPointDto> {
        return httpClient.get("$BASE_URL/api/gas-points/$id").body()
    }
    
    // Reservations Endpoints
    suspend fun createReservation(request: CreateReservationRequest): ApiResponse<ReservationDto> {
        return httpClient.post("$BASE_URL/api/reservations") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun getReservations(): ApiResponse<List<ReservationDto>> {
        return httpClient.get("$BASE_URL/api/reservations").body()
    }
    
    suspend fun getReservationById(id: String): ApiResponse<ReservationDto> {
        return httpClient.get("$BASE_URL/api/reservations/$id").body()
    }
    
    // Orders Endpoints
    suspend fun createOrder(request: CreateOrderRequest): ApiResponse<OrderDto> {
        return httpClient.post("$BASE_URL/api/orders") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun getUserOrders(): ApiResponse<List<OrderDto>> {
        return httpClient.get("$BASE_URL/api/orders").body()
    }
    
    suspend fun getOrderById(id: String): ApiResponse<OrderDto> {
        return httpClient.get("$BASE_URL/api/orders/$id").body()
    }
    
    suspend fun cancelOrder(id: String, reason: String): ApiResponse<OrderDto> {
        return httpClient.put("$BASE_URL/api/orders/$id/cancel") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("reason" to reason))
        }.body()
    }
    
    // Payment Endpoints
    suspend fun initiatePayment(request: InitiatePaymentRequest): ApiResponse<PaymentDto> {
        return httpClient.post("$BASE_URL/api/payments/initiate") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun confirmPayment(request: ConfirmPaymentRequest): ApiResponse<PaymentDto> {
        return httpClient.post("$BASE_URL/api/payments/confirm") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun getPaymentStatus(id: String): ApiResponse<PaymentDto> {
        return httpClient.get("$BASE_URL/api/payments/$id/status").body()
    }
}

// Ktor Client Provider
fun createHttpClient(getToken: suspend () -> String?): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
        
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
        
        install(Auth) {
            bearer {
                loadTokens {
                    val token = getToken()
                    token?.let { BearerTokens(it, it) }
                }
            }
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
        }
        
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }
}
