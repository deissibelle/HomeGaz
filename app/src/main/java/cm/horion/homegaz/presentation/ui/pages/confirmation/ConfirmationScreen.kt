package cm.horion.homegaz.presentation.ui.pages.confirmation


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.domain.model.distributor.OrderSummary
import cm.horion.homegaz.presentation.ui.components.common.HomeGazButton
import cm.horion.homegaz.presentation.ui.components.common.WarningNote
import cm.horion.homegaz.presentation.ui.components.confirmation.OrderRecapCard
import cm.horion.homegaz.presentation.ui.components.distributor.TotalAmountCard
import cm.horion.homegaz.presentation.ui.components.payment.PaymentTopBar

@Composable
fun ConfirmationScreen(
    summary        : OrderSummary,
    onBackClick    : () -> Unit = {},
    onModifyClick  : () -> Unit = {},
    onConfirmClick : () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        PaymentTopBar(
            title       = "Confirmation de paiement",
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        OrderRecapCard(summary = summary)

        Spacer(modifier = Modifier.height(24.dp))

        TotalAmountCard(total = summary.total)

        Spacer(modifier = Modifier.height(12.dp))

        WarningNote(
            message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                    "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                    "Ut enim ad minim veniam,"
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
                text         = "Modifier",
                onClick      = onModifyClick,
                isOutlined   = true,
                contentColor = Color(0xFF7E7E7E),
                modifier     = Modifier.weight(1f)
            )

            HomeGazButton(
                text     = "Confirmer",
                onClick  = onConfirmClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}