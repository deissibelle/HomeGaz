package com.homegaz.app.data.repository

import com.homegaz.app.data.local.HomeGazDatabase
import com.homegaz.app.data.local.PreferencesManager
import com.homegaz.app.data.local.entity.toEntity
import com.homegaz.app.data.local.entity.toDomain
import com.homegaz.app.data.remote.HomeGazApiClient
import com.homegaz.app.data.remote.dto.*
import com.homegaz.app.domain.model.*
import com.homegaz.app.domain.repository.AuthRepository
import com.homegaz.app.domain.repository.PasswordStrength
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: HomeGazApiClient,
    private val database: HomeGazDatabase,
    private val preferences: PreferencesManager
) : AuthRepository {

    override suspend fun registerWithEmail(email: String, password: String): Result<Unit> {
        return try {
            val response = api.registerWithEmail(RegisterEmailRequest(email, password))
            if (response.success) {
                Result.Success(Unit)
            } else {
                Result.Error(AppException.ServerException(response.error ?: "Erreur d'inscription"))
            }
        } catch (e: Exception) {
            Result.Error(AppException.NetworkException(e.message ?: "Erreur réseau"))
        }
    }

    override suspend fun sendEmailOtp(email: String): Result<Unit> {
        return try {
            val response = api.sendOtp(SendOtpRequest(email = email))
            if (response.success) Result.Success(Unit)
            else Result.Error(AppException.ServerException(response.error ?: "Erreur"))
        } catch (e: Exception) {
            Result.Error(AppException.NetworkException())
        }
    }

    override suspend fun verifyEmailOtp(email: String, otp: String, password: String): Result<AuthResult> {
        return try {
            val response = api.verifyOtp(VerifyOtpRequest(email = email, otp = otp, password = password))
            if (response.success && response.data != null) {
                val authResult = response.data.toDomain()
                saveAuthTokens(authResult.tokens)
                saveCurrentUser(authResult.user)
                Result.Success(authResult)
            } else {
                Result.Error(AppException.AuthException(response.error ?: "Code invalide"))
            }
        } catch (e: Exception) {
            Result.Error(AppException.NetworkException())
        }
    }

    override suspend fun registerWithPhone(phone: String, countryCode: String): Result<Unit> {
        return try {
            val response = api.registerWithPhone(RegisterPhoneRequest(phone, countryCode))
            if (response.success) Result.Success(Unit)
            else Result.Error(AppException.ServerException(response.error ?: "Erreur"))
        } catch (e: Exception) {
            Result.Error(AppException.NetworkException())
        }
    }

    override suspend fun sendPhoneOtp(phone: String, countryCode: String): Result<Unit> {
        return try {
            val response = api.sendOtp(SendOtpRequest(phone = phone, countryCode = countryCode))
            if (response.success) Result.Success(Unit)
            else Result.Error(AppException.ServerException(response.error ?: "Erreur"))
        } catch (e: Exception) {
            Result.Error(AppException.NetworkException())
        }
    }

    override suspend fun verifyPhoneOtp(phone: String, countryCode: String, otp: String): Result<AuthResult> {
        return try {
            val response = api.verifyOtp(VerifyOtpRequest(phone = phone, countryCode = countryCode, otp = otp))
            if (response.success && response.data != null) {
                val authResult = response.data.toDomain()
                saveAuthTokens(authResult.tokens)
                saveCurrentUser(authResult.user)
                Result.Success(authResult)
            } else {
                Result.Error(AppException.AuthException(response.error ?: "Code invalide"))
            }
        } catch (e: Exception) {
            Result.Error(AppException.NetworkException())
        }
    }

    override suspend fun loginWithEmail(email: String, password: String): Result<AuthResult> {
        return try {
            val response = api.loginWithEmail(LoginEmailRequest(email, password))
            if (response.success && response.data != null) {
                val authResult = response.data.toDomain()
                saveAuthTokens(authResult.tokens)
                saveCurrentUser(authResult.user)
                Result.Success(authResult)
            } else {
                Result.Error(AppException.AuthException(response.error ?: "Identifiants invalides"))
            }
        } catch (e: Exception) {
            Result.Error(AppException.NetworkException())
        }
    }

    override suspend fun loginWithPhone(phone: String, countryCode: String, otp: String): Result<AuthResult> {
        return try {
            val response = api.loginWithPhone(LoginPhoneRequest(phone, countryCode, otp))
            if (response.success && response.data != null) {
                val authResult = response.data.toDomain()
                saveAuthTokens(authResult.tokens)
                saveCurrentUser(authResult.user)
                Result.Success(authResult)
            } else {
                Result.Error(AppException.AuthException(response.error ?: "Code invalide"))
            }
        } catch (e: Exception) {
            Result.Error(AppException.NetworkException())
        }
    }

    override suspend fun sendPasswordResetOtpEmail(email: String): Result<Unit> = sendEmailOtp(email)
    override suspend fun sendPasswordResetOtpPhone(phone: String, countryCode: String): Result<Unit> = sendPhoneOtp(phone, countryCode)

    override suspend fun resetPasswordWithEmail(email: String, otp: String, newPassword: String): Result<AuthResult> {
        return try {
            val response = api.resetPassword(ResetPasswordRequest(email = email, otp = otp, newPassword = newPassword))
            if (response.success && response.data != null) {
                val authResult = response.data.toDomain()
                saveAuthTokens(authResult.tokens)
                saveCurrentUser(authResult.user)
                Result.Success(authResult)
            } else {
                Result.Error(AppException.AuthException(response.error ?: "Erreur"))
            }
        } catch (e: Exception) {
            Result.Error(AppException.NetworkException())
        }
    }

    override suspend fun resetPasswordWithPhone(phone: String, countryCode: String, otp: String, newPassword: String): Result<AuthResult> {
        return try {
            val response = api.resetPassword(ResetPasswordRequest(phone = phone, countryCode = countryCode, otp = otp, newPassword = newPassword))
            if (response.success && response.data != null) {
                val authResult = response.data.toDomain()
                saveAuthTokens(authResult.tokens)
                saveCurrentUser(authResult.user)
                Result.Success(authResult)
            } else {
                Result.Error(AppException.AuthException(response.error ?: "Erreur"))
            }
        } catch (e: Exception) {
            Result.Error(AppException.NetworkException())
        }
    }

    override suspend fun refreshToken(refreshToken: String): Result<AuthTokens> {
        return try {
            val response = api.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.success && response.data != null) {
                val tokens = response.data.toDomain()
                saveAuthTokens(tokens)
                Result.Success(tokens)
            } else {
                Result.Error(AppException.AuthException("Session expirée"))
            }
        } catch (e: Exception) {
            Result.Error(AppException.NetworkException())
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            clearAuthTokens()
            database.userDao().deleteAllUsers()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AppException.UnknownException())
        }
    }

    override suspend fun saveAuthTokens(tokens: AuthTokens) {
        preferences.saveAuthTokens(tokens)
    }

    override suspend fun getAuthTokens(): AuthTokens? = preferences.getAuthTokens()

    override suspend fun clearAuthTokens() = preferences.clearAuthTokens()

    override suspend fun saveCurrentUser(user: User) {
        preferences.saveCurrentUserId(user.id)
        database.userDao().insertUser(user.toEntity())
    }

    override suspend fun getCurrentUser(): User? {
        val userId = preferences.getCurrentUserId() ?: return null
        return database.userDao().getUserById(userId)?.toDomain()
    }

    override fun observeCurrentUser(): Flow<User?> {
        TODO("Not yet implemented")
    }

//    override fun observeCurrentUser(): Flow<User?> {
//        return preferences.getCurrentUserId().let { userId ->
//            database.userDao().observeUser(userId ?: "").map { it?.toDomain() }
//        }
//    }

    override fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^[0-9]{9}$"))
    }

    override fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    override fun getPasswordStrength(password: String): PasswordStrength {
        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }
        
        val score = listOf(hasUpper, hasLower, hasDigit, hasSpecial).count { it }
        return when {
            password.length < 8 -> PasswordStrength.WEAK
            score >= 3 && password.length >= 12 -> PasswordStrength.STRONG
            else -> PasswordStrength.MEDIUM
        }
    }
}

private fun AuthResponseDto.toDomain() = AuthResult(
    user = user.toDomain(),
    tokens = tokens.toDomain()
)

private fun UserDto.toDomain() = User(
    id = id,
    email = email,
    phone = phone,
    fullName = fullName,
    profilePicture = profilePicture,
    createdAt = createdAt,
    isEmailVerified = isEmailVerified,
    isPhoneVerified = isPhoneVerified
)

private fun TokensDto.toDomain() = AuthTokens(
    accessToken = accessToken,
    refreshToken = refreshToken,
    expiresIn = expiresIn
)
