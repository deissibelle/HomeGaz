package cm.horion.homegaz.presentation.ui.components.distributor


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.presentation.ui.theme.bodyFontFamily

@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .padding(horizontal = 20.dp)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = Color(0xFFE8E8E8),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            },
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Quantité",
            style = TextStyle(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = Color(0xFF717970)
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.one),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.Black
            )

            Spacer(modifier = Modifier.width(43.dp))

            Row(
                modifier = Modifier
                    .width(109.dp)
                    .height(35.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFB5B5B5),
                        shape = RoundedCornerShape(6.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Minus Button
                QuantityControlBtn(
                    label = "−",
                    onClick = { if (quantity > 1) onQuantityChange(quantity - 1) }
                )

                // Current Quantity
                Text(
                    text = quantity.toString(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = bodyFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                )

                // Plus Button
                QuantityControlBtn(
                    label = "+",
                    onClick = { onQuantityChange(quantity + 1) }
                )
            }
        }
    }
}

@Composable
private fun RowScope.QuantityControlBtn(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                lineHeight = 23.4.sp
            )
        )
    }
}
