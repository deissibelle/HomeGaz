package cm.horion.homegaz.presentation.ui.pages.distributor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.domain.model.distributor.DeliveryOption
import cm.horion.homegaz.domain.model.distributor.DistributorProduct
import cm.horion.homegaz.presentation.ui.components.common.HomeGazButton
import cm.horion.homegaz.presentation.ui.components.common.WarningNote
import cm.horion.homegaz.presentation.ui.components.distributor.DeliveryOptionRow
import cm.horion.homegaz.presentation.ui.components.distributor.DistributorHeader
import cm.horion.homegaz.presentation.ui.components.distributor.ProductInfoRow
import cm.horion.homegaz.presentation.ui.components.distributor.QuantitySelector
import cm.horion.homegaz.presentation.ui.components.distributor.TotalAmountCard

@Composable
fun DistributorPointDetailScreen(
    product    : DistributorProduct,
    onBackClick: () -> Unit,
    onNextClick: (quantity: Int, option: DeliveryOption) -> Unit
) {
    var quantity       by remember { mutableIntStateOf(1) }
    var selectedOption by remember { mutableStateOf(DeliveryOption.LIVRAISON) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        DistributorHeader(
            title       = "Algo Gaz",
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(4.dp))

        ProductInfoRow(label = "Marque", value = product.brand,  icon = Icons.Outlined.Settings)
        ProductInfoRow(label = "Poids",  value = product.weight, icon = Icons.Outlined.Scale)

        QuantitySelector(
            quantity         = quantity,
            onQuantityChange = { quantity = it }
        )

        DeliveryOptionRow(
            selectedOption   = selectedOption,
            onOptionSelected = { selectedOption = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        TotalAmountCard(total = product.unitPrice * quantity)

        Spacer(modifier = Modifier.height(12.dp))

        WarningNote(
            message = "Le paiement se fera à la livraison ou au retrait de votre bouteille"
        )

        Spacer(modifier = Modifier.height(32.dp))

        HomeGazButton(
            text     = "Suivant",
            onClick  = { onNextClick(quantity, selectedOption) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        )
    }
}