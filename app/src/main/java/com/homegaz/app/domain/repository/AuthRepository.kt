package com.homegaz.app.domain.repository

import com.homegaz.app.domain.model.AuthResult
import com.homegaz.app.domain.model.AuthTokens
import com.homegaz.app.domain.model.Result
import com.homegaz.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    
    // Email Registration
    suspend fun registerWithEmail(email: String, password: String): Result<Unit>
    suspend fun sendEmailOtp(email: String): Result<Unit>
    suspend fun verifyEmailOtp(email: String, otp: String, password: String): Result<AuthResult>
    
    // Phone Registration
    suspend fun registerWithPhone(phone: String, countryCode: String): Result<Unit>
    suspend fun sendPhoneOtp(phone: String, countryCode: String): Result<Unit>
    suspend fun verifyPhoneOtp(phone: String, countryCode: String, otp: String): Result<AuthResult>
    
    // Login
    suspend fun loginWithEmail(email: String, password: String): Result<AuthResult>
    suspend fun loginWithPhone(phone: String, countryCode: String, otp: String): Result<AuthResult>
    
    // Password Reset
    suspend fun sendPasswordResetOtpEmail(email: String): Result<Unit>
    suspend fun sendPasswordResetOtpPhone(phone: String, countryCode: String): Result<Unit>
    suspend fun resetPasswordWithEmail(email: String, otp: String, newPassword: String): Result<AuthResult>
    suspend fun resetPasswordWithPhone(phone: String, countryCode: String, otp: String, newPassword: String): Result<AuthResult>
    
    // Token Management
    suspend fun refreshToken(refreshToken: String): Result<AuthTokens>
    suspend fun logout(): Result<Unit>
    
    // Session
    suspend fun saveAuthTokens(tokens: AuthTokens)
    suspend fun getAuthTokens(): AuthTokens?
    suspend fun clearAuthTokens()
    suspend fun saveCurrentUser(user: User)
    suspend fun getCurrentUser(): User?
    fun observeCurrentUser(): Flow<User?>
    
    // Validation
    fun isValidEmail(email: String): Boolean
    fun isValidPhone(phone: String): Boolean
    fun isValidPassword(password: String): Boolean
    fun getPasswordStrength(password: String): PasswordStrength
}

enum class PasswordStrength {
    WEAK,
    MEDIUM,
    STRONG
}
