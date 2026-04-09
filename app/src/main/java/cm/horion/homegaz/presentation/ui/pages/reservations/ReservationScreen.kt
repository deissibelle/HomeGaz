package cm.horion.homegaz.presentation.ui.pages.reservations

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.auth.AuthContext
import cm.horion.homegaz.presentation.ui.pages.auth.AuthGuardScreen

@Composable
fun ReservationsScreen(navController: NavController) {
    val isLoggedIn by remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        val reservationContext = AuthContext(
            title = stringResource(R.string.auth_title_reservations),
            description = stringResource(R.string.auth_desc_reservations),
            icon = Icons.Default.ReceiptLong
        )

        AuthGuardScreen(
            authContext = reservationContext,
            onLoginClick = {
                // navController.navigate(Screen.Login.route)
            },
            onRegisterClick = {
                // navController.navigate(Screen.Register.route)
            },
            onForgotPasswordClick = {
                // Logic for forgot password
            }
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Vos Réservations",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
