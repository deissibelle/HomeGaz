package cm.horion.homegaz.presentation.ui.pages.distributor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.domain.model.DeliveryOption
import cm.horion.homegaz.domain.model.DistributorProduct
import cm.horion.homegaz.presentation.ui.components.common.HomeGazButton
import cm.horion.homegaz.presentation.ui.components.distributor.DeliveryOptionRow
import cm.horion.homegaz.presentation.ui.components.distributor.DistributorHeader
import cm.horion.homegaz.presentation.ui.components.distributor.ProductInfoRow
import cm.horion.homegaz.presentation.ui.components.distributor.QuantitySelector
import cm.horion.homegaz.presentation.ui.components.distributor.TotalAmountCard
import cm.horion.homegaz.presentation.ui.theme.bodyFontFamily

private val WarningAmber = Color(0xFFF59E0B)

@Composable
fun DistributorPointDetailScreen(
    product: DistributorProduct = DistributorProduct(
        brand = "SCTM",
        weight = "12,5kg",
        unitPrice = 7500
    ),
    onBackClick: () -> Unit,
    onNextClick: (quantity: Int, option: DeliveryOption) -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }
    var selectedOption by remember { mutableStateOf(DeliveryOption.LIVRAISON) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        DistributorHeader(onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(4.dp))

        ProductInfoRow(
            label = "Marque",
            value = product.brand,
            icon = Icons.Outlined.Settings
        )

        ProductInfoRow(
            label = "Poids",
            value = product.weight,
            icon = Icons.Outlined.Scale
        )

        QuantitySelector(
            quantity = quantity,
            onQuantityChange = { quantity = it }
        )

        DeliveryOptionRow(
            selectedOption = selectedOption,
            onOptionSelected = { selectedOption = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        TotalAmountCard(total = product.unitPrice * quantity)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                modifier = Modifier
                    .size(14.dp)
                    .padding(top = 1.dp),
                tint = WarningAmber
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Le paiement se fera à la livraison ou au retrait de votre bouteille",
                style = TextStyle(
                    fontFamily = bodyFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = WarningAmber,
                    textAlign = TextAlign.Start
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        HomeGazButton(
            text = "Suivant",
            onClick = { onNextClick(quantity, selectedOption) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        )
    }
}