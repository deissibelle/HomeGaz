package cm.horion.homegaz.presentation.ui.distribution


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.DeliveryOption
import cm.horion.homegaz.domain.model.DistributorProduct
import cm.horion.homegaz.presentation.ui.components.common.HomeGazButton
import cm.horion.homegaz.presentation.ui.components.distributor.DeliveryOptionRow
import cm.horion.homegaz.presentation.ui.components.distributor.DistributorHeader
import cm.horion.homegaz.presentation.ui.components.distributor.ProductInfoRow
import cm.horion.homegaz.presentation.ui.components.distributor.QuantitySelector
import cm.horion.homegaz.presentation.ui.components.distributor.TotalAmountCard
import cm.horion.homegaz.presentation.ui.theme.bodyFontFamily
@Composable
fun DistributorPointDetailScreen(
    product: DistributorProduct = DistributorProduct(
        "Algo Gaz",
        weight = "6kg",
        unitPrice = 7500,
    ),
    onBackClick: () -> Unit,
    onNextClick: (Int, DeliveryOption) -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }
    var selectedOption by remember { mutableStateOf(DeliveryOption.LIVRAISON) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        DistributorHeader( onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(24.dp))

        ProductInfoRow(label = "Marque", value = product.brand, iconResId = R.drawable.check, badgeWidth = 46)
        ProductInfoRow(label = "Poids", value = product.weight, iconResId = R.drawable.vector, badgeWidth = 50)

        QuantitySelector(quantity = quantity, onQuantityChange = { quantity = it })
        DeliveryOptionRow(selectedOption = selectedOption, onOptionSelected = { selectedOption = it })

        Spacer(modifier = Modifier.height(32.dp))

        TotalAmountCard(total = product.unitPrice * quantity)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.vector),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color(0xFFF59E0B)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Le paiement se fera a la livraison ou au retrait de votre bouteille",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = bodyFontFamily,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = Color(0xFFF59E0B),
                    textAlign = TextAlign.Center
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        HomeGazButton(
            text = "Suivant",
            onClick = { onNextClick(quantity, selectedOption) },
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        )
    }
}
