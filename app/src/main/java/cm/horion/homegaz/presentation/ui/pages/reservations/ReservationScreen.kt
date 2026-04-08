package cm.horion.homegaz.presentation.ui.pages.reservations


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cm.horion.homegaz.domain.model.auth.AuthContext
import cm.horion.homegaz.presentation.ui.pages.auth.AuthGuardScreen
import cm.horion.homegaz.R

@Composable
fun ReservationsScreen() {
    val reservationContext = AuthContext(
        title = stringResource(R.string.auth_title_reservations),
        description = stringResource(R.string.auth_desc_reservations),
        icon = Icons.Default.ReceiptLong
    )
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AuthGuardScreen(
            authContext = reservationContext,
            onLoginClick = {},
            onRegisterClick = {},
            onForgotPasswordClick = {})

    }
}