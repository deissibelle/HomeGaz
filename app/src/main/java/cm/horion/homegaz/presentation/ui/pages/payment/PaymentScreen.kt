package cm.horion.homegaz.presentation.ui.pages.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.presentation.ui.components.common.HomeGazButton
import cm.horion.homegaz.presentation.ui.components.common.WarningNote
import cm.horion.homegaz.presentation.ui.components.distributor.TotalAmountCard
import cm.horion.homegaz.presentation.ui.components.payment.PaymentOptionRow
import cm.horion.homegaz.presentation.ui.components.payment.PaymentTopBar
import cm.horion.homegaz.presentation.ui.components.payment.PhoneNumberField
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.presentation.state.DistributorDetailUiState
import cm.horion.homegaz.util.phoneErrorMessage

@Composable
fun PaymentScreen(
    uiState        : DistributorDetailUiState,
    onPhoneChange  : (String) -> Unit = {},
    onMethodSelected : (PaymentMethod) -> Unit = {},
    onBackClick    : () -> Unit = {},
    onNextClick    : () -> Unit = {},
) {

    //val uiState by viewModel.uiState.collectAsState()

    val phoneError = phoneErrorMessage(uiState.phoneNumber, uiState.selectedMethod)
    //val phoneError = viewModel.phoneErrorMessage(uiState.phoneNumber, uiState.selectedMethod)

    PaymentContent(
        uiState               = uiState,
        phoneError            = phoneError,
        onBackClick           = onBackClick,
        onNextClick           = {
            onNextClick()
        },
        onPaymentMethodChange = onMethodSelected,
        onPhoneNumberChange   = onPhoneChange
    )
}

@Composable
private fun PaymentContent(
    uiState               : DistributorDetailUiState,
    phoneError            : String?,
    onBackClick           : () -> Unit,
    onNextClick           : () -> Unit,
    onPaymentMethodChange : (PaymentMethod) -> Unit,
    onPhoneNumberChange   : (String) -> Unit
) {
    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

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
            selectedMethod   = uiState.selectedMethod,
            onMethodSelected = onPaymentMethodChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        PhoneNumberField(
            value = uiState.phoneNumber,
            onValueChange = onPhoneNumberChange,
            errorMessage = phoneError
        )

        Spacer(modifier = Modifier.height(20.dp))

        TotalAmountCard(total = uiState.total)

        Spacer(modifier = Modifier.height(12.dp))

        WarningNote(
            message = stringResource(R.string.payment_warning_note)
        )

        Spacer(modifier = Modifier.weight(1f))

        HomeGazButton(
            text     = stringResource(R.string.next),
            onClick  = onNextClick,
            enabled  = uiState.isFormValid && phoneError == null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        )
    }
}