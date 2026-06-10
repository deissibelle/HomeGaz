package cm.horion.homegaz.presentation.ui.pages.payment

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.presentation.ui.components.common.HomeGazButton
import cm.horion.homegaz.presentation.ui.theme.SuccessColor
import cm.horion.homegaz.R
import cm.horion.homegaz.presentation.ui.theme.poppinsFontFamily


@Composable
fun PaymentSuccessScreen(
    isSuccess          : Boolean = true, // 🔥 Ajout pour piloter le mode
    onCloseClick       : () -> Unit = {},
    onReservationsClick: () -> Unit = {},
    errorMsg           : String? = null // Optionnel : pour afficher le vrai message de l'API en cas d'échec
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        Box(
            modifier         = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(39.dp)
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center
        ) {
            Text(
                // 🛠️ Titre dynamique (ex: "Paiement Réussi" ou "Échec du Paiement")
                text     = if (isSuccess) stringResource(R.string.payment_success_title) else "Échec du paiement",
                modifier = Modifier.padding(horizontal = 56.dp),
                style    = TextStyle(
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 20.sp,
                    lineHeight = (20 * 1.17).sp,
                    color      = MaterialTheme.colorScheme.primary,
                    textAlign  = TextAlign.Center
                )
            )

            IconButton(
                onClick  = onCloseClick,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
                    .size(width = 35.dp, height = 27.dp)
            ) {
                Icon(
                    imageVector        = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.close_description),
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(20.dp)
                )
            }
        }

        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(top = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🛠️ Couleur et Icône dynamiques
            val statusColor = if (isSuccess) SuccessColor else MaterialTheme.colorScheme.error
            val statusIcon  = if (isSuccess) Icons.Filled.Check else Icons.Filled.Close

            Box(
                modifier         = Modifier
                    .size(58.dp)
                    .border(
                        width = 1.93.dp,
                        color = statusColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = statusIcon,
                    contentDescription = null,
                    tint               = statusColor,
                    modifier           = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                // 🛠️ Message dynamique
                text = if (isSuccess) stringResource(R.string.payment_success_message) else (errorMsg ?: "Le paiement a échoué. Veuillez réessayer."),
                modifier = Modifier.width(294.dp),
                style    = TextStyle(
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize   = 14.sp,
                    lineHeight = (14 * 1.17).sp,
                    color      = MaterialTheme.colorScheme.primary,
                    textAlign  = TextAlign.Center
                )
            )
        }

        // 🛠️ Bouton dynamique : Si échec, on propose de "Réessayer" au lieu d'aller aux réservations
        HomeGazButton(
            text     = if (isSuccess) stringResource(R.string.btn_my_reservations) else "Réessayer le paiement",
            onClick  = onReservationsClick, // Le callback servira à ré-aiguiller
            modifier = Modifier
                .width(269.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PaymentSuccessPreview() {
    PaymentSuccessScreen ()
}