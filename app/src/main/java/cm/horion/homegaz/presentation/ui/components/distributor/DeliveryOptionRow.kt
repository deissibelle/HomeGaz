package cm.horion.homegaz.presentation.ui.components.distributor


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.domain.model.DeliveryOption
import cm.horion.homegaz.presentation.ui.theme.bodyFontFamily

@Composable
fun DeliveryOptionRow(
    selectedOption: DeliveryOption,
    onOptionSelected: (DeliveryOption) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(80.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Option",
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = Color(0xFF717970)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OptionCard(
                label = "Livraison",
                isSelected = selectedOption == DeliveryOption.LIVRAISON,
                onClick = { onOptionSelected(DeliveryOption.LIVRAISON) },
                modifier = Modifier.weight(1f)
            )

            OptionCard(
                label = "Retrait",
                isSelected = selectedOption == DeliveryOption.RETRAIT,
                onClick = { onOptionSelected(DeliveryOption.RETRAIT) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OptionCard(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Color.White else Color(0xFFAFB0B1)
    val contentColor = if (isSelected) Color(0xFF003761) else Color.White
    val borderColor = if (isSelected) Color(0xFF003761) else Color.Transparent

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 18.72.sp,
                color = contentColor
            )
        )
    }
}
