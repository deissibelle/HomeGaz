package cm.horion.homegaz.data.security

sealed class AuthState {
    object Unauthenticated : AuthState()
    object Checking : AuthState()
    object Authenticated : AuthState()
}