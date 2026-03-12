package cm.horion.homegaz.presentation.ui.components.distributor

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.domain.model.distributor.DeliveryOption
import cm.horion.homegaz.presentation.ui.components.common.OptionSelectorRow


@Composable
fun DeliveryOptionRow(
    selectedOption: DeliveryOption,
    onOptionSelected: (DeliveryOption) -> Unit
) {
    val options = listOf("Livraison", "Retrait")
    val selectedIndex = if (selectedOption == DeliveryOption.LIVRAISON) 0 else 1

    OptionSelectorRow(
        label = "Option",
        options = options,
        selectedIndex = selectedIndex,
        onSelect = { index ->
            onOptionSelected(if (index == 0) DeliveryOption.LIVRAISON else DeliveryOption.RETRAIT)
        },
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(80.dp)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = Color(0xFFE8E8E8),
                    start = Offset(0f, size.height - strokeWidth / 2),
                    end = Offset(size.width, size.height - strokeWidth / 2),
                    strokeWidth = strokeWidth
                )
            }
    )
}