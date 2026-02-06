package com.homegaz.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homegaz.app.domain.model.AuthResult
import com.homegaz.app.domain.model.Result
import com.homegaz.app.domain.repository.AuthRepository
import com.homegaz.app.domain.repository.PasswordStrength
import com.homegaz.app.domain.usecase.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val otpSent: Boolean = false,
    val isAuthenticated: Boolean = false,
    val passwordStrength: PasswordStrength = PasswordStrength.WEAK
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerWithEmailUseCase: RegisterWithEmailUseCase,
    private val verifyEmailOtpUseCase: VerifyEmailOtpUseCase,
    private val registerWithPhoneUseCase: RegisterWithPhoneUseCase,
    private val verifyPhoneOtpUseCase: VerifyPhoneOtpUseCase,
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val loginWithPhoneUseCase: LoginWithPhoneUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val getPasswordStrengthUseCase: GetPasswordStrengthUseCase,
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    // INSCRIPTION PAR EMAIL
    fun registerWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = registerWithEmailUseCase(email, password)) {
                is Result.Success -> {
                    // Envoyer automatiquement le code OTP
                    sendEmailOtp(email)
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.exception.message) 
                    }
                }
                Result.Loading -> {}
            }
        }
    }

    fun sendEmailOtp(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = repository.sendEmailOtp(email)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(isLoading = false, otpSent = true) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.exception.message) 
                    }
                }
                Result.Loading -> {}
            }
        }
    }

    fun verifyEmailOtp(email: String, otp: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = verifyEmailOtpUseCase(email, otp, password)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(isLoading = false, isAuthenticated = true) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.exception.message) 
                    }
                }
                Result.Loading -> {}
            }
        }
    }

    // INSCRIPTION PAR TÉLÉPHONE
    fun registerWithPhone(phone: String, countryCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = registerWithPhoneUseCase(phone, countryCode)) {
                is Result.Success -> {
                    sendPhoneOtp(phone, countryCode)
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.exception.message) 
                    }
                }
                Result.Loading -> {}
            }
        }
    }

    fun sendPhoneOtp(phone: String, countryCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = repository.sendPhoneOtp(phone, countryCode)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(isLoading = false, otpSent = true) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.exception.message) 
                    }
                }
                Result.Loading -> {}
            }
        }
    }

    fun verifyPhoneOtp(phone: String, countryCode: String, otp: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = verifyPhoneOtpUseCase(phone, countryCode, otp)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(isLoading = false, isAuthenticated = true) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.exception.message) 
                    }
                }
                Result.Loading -> {}
            }
        }
    }

    // CONNEXION PAR EMAIL
    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = loginWithEmailUseCase(email, password)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(isLoading = false, isAuthenticated = true) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.exception.message) 
                    }
                }
                Result.Loading -> {}
            }
        }
    }

    // CONNEXION PAR TÉLÉPHONE
    fun loginWithPhone(phone: String, countryCode: String, otp: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = loginWithPhoneUseCase(phone, countryCode, otp)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(isLoading = false, isAuthenticated = true) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.exception.message) 
                    }
                }
                Result.Loading -> {}
            }
        }
    }

    // RÉINITIALISATION MOT DE PASSE
    fun resetPasswordWithEmail(email: String, otp: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = resetPasswordUseCase.withEmail(email, otp, newPassword)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(isLoading = false, isAuthenticated = true) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.exception.message) 
                    }
                }
                Result.Loading -> {}
            }
        }
    }

    fun resetPasswordWithPhone(phone: String, countryCode: String, otp: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = resetPasswordUseCase.withPhone(phone, countryCode, otp, newPassword)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(isLoading = false, isAuthenticated = true) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.exception.message) 
                    }
                }
                Result.Loading -> {}
            }
        }
    }

    // FORCE DU MOT DE PASSE
    fun updatePasswordStrength(password: String) {
        val strength = getPasswordStrengthUseCase(password)
        _uiState.update { it.copy(passwordStrength = strength) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetOtpSent() {
        _uiState.update { it.copy(otpSent = false) }
    }
}
