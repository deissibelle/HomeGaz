package com.homegaz.app.domain.model

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: AppException) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

sealed class AppException(override val message: String) : Exception(message) {
    data class NetworkException(override val message: String = "Erreur de connexion") : AppException(message)
    data class ServerException(override val message: String = "Erreur serveur") : AppException(message)
    data class AuthException(override val message: String = "Erreur d'authentification") : AppException(message)
    data class ValidationException(override val message: String = "Données invalides") : AppException(message)
    data class NotFoundException(override val message: String = "Ressource introuvable") : AppException(message)
    data class UnknownException(override val message: String = "Erreur inconnue") : AppException(message)
}

fun <T> Result<T>.getOrNull(): T? = when (this) {
    is Result.Success -> data
    else -> null
}

fun <T> Result<T>.isSuccess(): Boolean = this is Result.Success
fun <T> Result<T>.isError(): Boolean = this is Result.Error
fun <T> Result<T>.isLoading(): Boolean = this is Result.Loading
