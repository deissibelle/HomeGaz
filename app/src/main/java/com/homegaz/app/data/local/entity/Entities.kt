package com.homegaz.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.homegaz.app.domain.model.*

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String?,
    val phone: String?,
    val fullName: String?,
    val profilePicture: String?,
    val createdAt: Long,
    val isEmailVerified: Boolean,
    val isPhoneVerified: Boolean
)

@Entity(tableName = "gas_points")
data class GasPointEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val brand: String, // Stored as String, converted to enum
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val phone: String,
    val openingHours: String,
    val isOpen: Boolean,
    val availableWeights: String, // Comma-separated: "6,12,25,38"
    val rating: Float,
    val reviewsCount: Int,
    val pricePerKg: Double,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reservations")
data class ReservationEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val gasPointId: String,
    val brand: String,
    val weight: Int,
    val quantity: Int,
    val deliveryMode: String,
    val needsConsigne: Boolean,
    val consigneAmount: Int,
    val subtotal: Int,
    val deliveryFee: Int,
    val total: Int,
    val deliveryAddress: String?,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val notes: String?,
    val createdAt: Long,
    val status: String
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val reservationId: String,
    val paymentMethod: String,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long,
    val estimatedDeliveryTime: Long?,
    val actualDeliveryTime: Long?,
    val cancelReason: String?
)

// Mappers
fun UserEntity.toDomain() = User(
    id = id,
    email = email,
    phone = phone,
    fullName = fullName,
    profilePicture = profilePicture,
    createdAt = createdAt,
    isEmailVerified = isEmailVerified,
    isPhoneVerified = isPhoneVerified
)

fun User.toEntity() = UserEntity(
    id = id,
    email = email,
    phone = phone,
    fullName = fullName,
    profilePicture = profilePicture,
    createdAt = createdAt,
    isEmailVerified = isEmailVerified,
    isPhoneVerified = isPhoneVerified
)

fun GasPointEntity.toDomain() = GasPoint(
    id = id,
    name = name,
    brand = GasBrand.valueOf(brand),
    latitude = latitude,
    longitude = longitude,
    address = address,
    phone = phone,
    openingHours = openingHours,
    isOpen = isOpen,
    availableWeights = availableWeights.split(",")
        .mapNotNull { it.toIntOrNull() }
        .mapNotNull { GasWeight.fromKg(it) },
    rating = rating,
    reviewsCount = reviewsCount,
    pricePerKg = pricePerKg
)

fun GasPoint.toEntity() = GasPointEntity(
    id = id,
    name = name,
    brand = brand.name,
    latitude = latitude,
    longitude = longitude,
    address = address,
    phone = phone,
    openingHours = openingHours,
    isOpen = isOpen,
    availableWeights = availableWeights.joinToString(",") { it.kg.toString() },
    rating = rating,
    reviewsCount = reviewsCount,
    pricePerKg = pricePerKg
)
