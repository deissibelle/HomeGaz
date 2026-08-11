package cm.horion.homegaz.presentation.ui.pages.confirmation


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cm.horion.homegaz.R
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.presentation.state.DistributorDetailUiState
import cm.horion.homegaz.presentation.ui.components.common.HomeGazButton
import cm.horion.homegaz.presentation.ui.components.common.WarningNote
import cm.horion.homegaz.presentation.ui.components.confirmation.OrderRecapCard
import cm.horion.homegaz.presentation.ui.components.distributor.TotalAmountCard
import cm.horion.homegaz.presentation.ui.components.payment.PaymentTopBar
import cm.horion.homegaz.presentation.viewmodel.DistributorDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ConfirmationScreen(
    uiState        : DistributorDetailUiState,
    onBackClick    : () -> Unit = {},
    onModifyClick  : () -> Unit = {},
    onConfirmClick : () -> Unit = {},
    onStartOrder   : () -> Unit = {},
    onStartPayment : () -> Unit = {},
    dismissError   : () -> Unit = {},
    viewModel      : DistributorDetailViewModel = koinViewModel()
) {
    // Écoute uniquement le succès de la commande
    LaunchedEffect(uiState.isOrderSuccess) {
        if (uiState.isOrderSuccess) {
            onStartPayment()
            Log.d("PAYEMENT", "order reussi")
        }
    }

// Écoute uniquement le lancement du paiement
    LaunchedEffect(uiState.isPaymentSuccessLancer) {
        if (uiState.isPaymentSuccessLancer) {
            viewModel.checkPaymentStatus()
            onConfirmClick()
            Log.d("PAYEMENT", "payement lancer")
        }
    }

    if (!uiState.error.isNullOrBlank()) {
        AlertDialog(
            onDismissRequest = { dismissError() },
            title = { Text(text = stringResource(R.string.error_dialog_title)) },
            text = { Text(text = uiState.error) },
            confirmButton = {
                TextButton(onClick = { dismissError() }) {
                    Text(
                        text = stringResource(R.string.error_dialog_confirm),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        PaymentTopBar(
            title       = stringResource(R.string.confirmation_title),
            onBackClick = { if (!uiState.isProcessingPay) onBackClick() }
        )

        Spacer(modifier = Modifier.height(32.dp))

        OrderRecapCard(summary = uiState)

        Spacer(modifier = Modifier.height(24.dp))

        TotalAmountCard(total = uiState.total)

        Spacer(modifier = Modifier.height(12.dp))

        WarningNote(
            message = stringResource(R.string.confirmation_warning_note)
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            HomeGazButton(
                text         = stringResource(R.string.btn_modify),
                onClick      = onModifyClick,
                isOutlined   = true,
                enabled      = !uiState.isProcessingPay,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier     = Modifier.weight(1f)
            )

            // Bouton Confirmer / Loader
            if (uiState.isProcessingPay) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            } else {
                HomeGazButton(
                    text     = stringResource(R.string.btn_confirm),
                    onClick  = onStartOrder,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}