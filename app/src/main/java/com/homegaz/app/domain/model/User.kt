package com.homegaz.app.domain.model

data class User(
    val id: String,
    val email: String? = null,
    val phone: String? = null,
    val fullName: String? = null,
    val profilePicture: String? = null,
    val createdAt: Long,
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false
)

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

data class AuthResult(
    val user: User,
    val tokens: AuthTokens
)
