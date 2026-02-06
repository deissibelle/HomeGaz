package com.homegaz.app.domain.usecase.auth

import com.homegaz.app.domain.model.AuthResult
import com.homegaz.app.domain.model.Result
import com.homegaz.app.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterWithEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        if (!repository.isValidEmail(email)) {
            return Result.Error(com.homegaz.app.domain.model.AppException.ValidationException("Email invalide"))
        }
        if (!repository.isValidPassword(password)) {
            return Result.Error(com.homegaz.app.domain.model.AppException.ValidationException("Mot de passe trop faible"))
        }
        return repository.registerWithEmail(email, password)
    }
}

class VerifyEmailOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, otp: String, password: String): Result<AuthResult> {
        if (otp.length != 6) {
            return Result.Error(com.homegaz.app.domain.model.AppException.ValidationException("Code OTP invalide"))
        }
        return repository.verifyEmailOtp(email, otp, password)
    }
}

class RegisterWithPhoneUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(phone: String, countryCode: String): Result<Unit> {
        if (!repository.isValidPhone(phone)) {
            return Result.Error(com.homegaz.app.domain.model.AppException.ValidationException("Numéro invalide"))
        }
        return repository.registerWithPhone(phone, countryCode)
    }
}

class VerifyPhoneOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(phone: String, countryCode: String, otp: String): Result<AuthResult> {
        if (otp.length != 6) {
            return Result.Error(com.homegaz.app.domain.model.AppException.ValidationException("Code OTP invalide"))
        }
        return repository.verifyPhoneOtp(phone, countryCode, otp)
    }
}

class LoginWithEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthResult> {
        if (!repository.isValidEmail(email)) {
            return Result.Error(com.homegaz.app.domain.model.AppException.ValidationException("Email invalide"))
        }
        if (password.isBlank()) {
            return Result.Error(com.homegaz.app.domain.model.AppException.ValidationException("Mot de passe requis"))
        }
        return repository.loginWithEmail(email, password)
    }
}

class LoginWithPhoneUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(phone: String, countryCode: String, otp: String): Result<AuthResult> {
        if (!repository.isValidPhone(phone)) {
            return Result.Error(com.homegaz.app.domain.model.AppException.ValidationException("Numéro invalide"))
        }
        if (otp.length != 6) {
            return Result.Error(com.homegaz.app.domain.model.AppException.ValidationException("Code OTP invalide"))
        }
        return repository.loginWithPhone(phone, countryCode, otp)
    }
}

class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend fun withEmail(email: String, otp: String, newPassword: String): Result<AuthResult> {
        if (!repository.isValidEmail(email)) {
            return Result.Error(com.homegaz.app.domain.model.AppException.ValidationException("Email invalide"))
        }
        if (!repository.isValidPassword(newPassword)) {
            return Result.Error(com.homegaz.app.domain.model.AppException.ValidationException("Mot de passe trop faible"))
        }
        return repository.resetPasswordWithEmail(email, otp, newPassword)
    }
    
    suspend fun withPhone(phone: String, countryCode: String, otp: String, newPassword: String): Result<AuthResult> {
        if (!repository.isValidPhone(phone)) {
            return Result.Error(com.homegaz.app.domain.model.AppException.ValidationException("Numéro invalide"))
        }
        if (!repository.isValidPassword(newPassword)) {
            return Result.Error(com.homegaz.app.domain.model.AppException.ValidationException("Mot de passe trop faible"))
        }
        return repository.resetPasswordWithPhone(phone, countryCode, otp, newPassword)
    }
}

class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.getCurrentUser()
    fun observe() = repository.observeCurrentUser()
}

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.logout()
}

class GetPasswordStrengthUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(password: String) = repository.getPasswordStrength(password)
}
