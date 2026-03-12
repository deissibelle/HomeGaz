package cm.horion.homegaz.presentation.ui.pages.reservations


import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.presentation.ui.theme.bodyFontFamily

@Composable
fun ReservationsScreen() {
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text  = "Mes réservations",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 18.sp,
                color      = MaterialTheme.colorScheme.primary
            )
        )
    }
}