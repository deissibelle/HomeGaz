package cm.horion.homegaz.presentation.ui.pages.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.domain.model.DeliveryOption
import cm.horion.homegaz.domain.model.OrderSummary
import cm.horion.homegaz.domain.model.PaymentMethod
import cm.horion.homegaz.presentation.ui.components.common.HomeGazButton
import cm.horion.homegaz.presentation.ui.components.common.WarningNote
import cm.horion.homegaz.presentation.ui.components.distributor.TotalAmountCard
import cm.horion.homegaz.presentation.ui.components.payment.PaymentOptionRow
import cm.horion.homegaz.presentation.ui.components.payment.PaymentTopBar
import cm.horion.homegaz.presentation.ui.components.payment.PhoneNumberField

@Composable
fun PaymentScreen(
    brand          : String,
    weight         : String,
    quantity       : Int,
    deliveryOption : DeliveryOption,
    unitPrice      : Int,
    onBackClick    : () -> Unit = {},
    onNextClick    : (summary: OrderSummary) -> Unit = {}
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.ORANGE_MONEY) }
    var phoneNumber    by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        PaymentTopBar(onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(16.dp))

        PaymentOptionRow(
            selectedMethod   = selectedMethod,
            onMethodSelected = { selectedMethod = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        PhoneNumberField(
            value         = phoneNumber,
            onValueChange = { phoneNumber = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        TotalAmountCard(total = unitPrice * quantity)

        Spacer(modifier = Modifier.height(12.dp))

        WarningNote(
            message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                    "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                    "Ut enim ad minim veniam,"
        )

        Spacer(modifier = Modifier.weight(1f))

        HomeGazButton(
            text    = "Suivant",
            onClick = {
                onNextClick(
                    OrderSummary(
                        brand          = brand,
                        weight         = weight,
                        quantity       = quantity,
                        deliveryOption = deliveryOption,
                        paymentMethod  = selectedMethod,
                        phoneNumber    = phoneNumber,
                        unitPrice      = unitPrice
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        )
    }
}