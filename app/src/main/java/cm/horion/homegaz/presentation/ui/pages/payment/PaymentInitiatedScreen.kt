package cm.horion.homegaz.presentation.ui.pages.payment

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.presentation.ui.components.common.HomeGazButton
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.payment.dto.PaymentStatus
import cm.horion.homegaz.presentation.state.DistributorDetailUiState
import cm.horion.homegaz.presentation.ui.theme.poppinsFontFamily


private fun ussdCode(method: PaymentMethod) = when (method) {
    PaymentMethod.OM    -> "*150*50#"   // sans le # initial qui cause des soucis d'encodage
    PaymentMethod.MOMO  -> "*126#"
}

@Composable
fun PaymentInitiatedScreen(
    uiState      : DistributorDetailUiState,
    paymentMethod: PaymentMethod = PaymentMethod.OM,
    onDone: () -> Unit = {},
    onEchec: () -> Unit = {}
) {
    val context = LocalContext.current
    val ussd    = ussdCode(paymentMethod)

    val steps = listOf(
        stringResource(R.string.payment_step_1),
        stringResource(R.string.payment_step_2),
        stringResource(R.string.payment_step_3, ussd)
    )


    LaunchedEffect(uiState.isPaySuccess) {
        if(uiState.isPaySuccess == PaymentStatus.SUCCESS) {
            onDone()
        }

        if(uiState.isPaySuccess == PaymentStatus.FAILED) {
            onEchec()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text  = stringResource(R.string.payment_initiated_title),
            style = TextStyle(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 20.sp,
                lineHeight = (20 * 1.17).sp,
                color      = MaterialTheme.colorScheme.primary,
                textAlign  = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            imageVector        = Icons.Outlined.HourglassTop,
            contentDescription = null,
            modifier           = Modifier.size(58.dp),
            tint               =  MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            steps.forEachIndexed { index, text ->
                StepRow(number = index + 1, text = text)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        HomeGazButton(
            text    = stringResource(R.string.compose_ussd, ussd),
            onClick = {

                val encodedUssd = ussd.replace("#", "%23")
                val uri = Uri.parse("tel:$encodedUssd")
                val intent = Intent(Intent.ACTION_CALL, uri)
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    val dialIntent = Intent(Intent.ACTION_DIAL, uri)
                    context.startActivity(dialIntent)
                }
            },
            modifier = Modifier
                .width(320.dp)
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
private fun StepRow(number: Int, text: String) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier         = Modifier
                .size(44.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text  = number.toString(),
                style = TextStyle(
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 20.sp,
                    textAlign  = TextAlign.Center
                )
            )
        }

        Text(
            text  = text,
            style = TextStyle(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize   = 14.sp,
                lineHeight = (14 * 1.17).sp,
                color      = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun PaymentInitiatedScreenPreview() {
    // Remplace par le nom exact de ton thème Compose s'il est différent (ex: HomeGazTheme)
    MaterialTheme {
        PaymentInitiatedScreen(
            uiState = DistributorDetailUiState(),
            onDone = { /* Action simulée pour la preview */ }
        )
    }
}