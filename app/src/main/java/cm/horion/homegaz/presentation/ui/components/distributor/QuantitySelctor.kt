package cm.horion.homegaz.presentation.ui.components.distributor

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.presentation.ui.theme.bodyFontFamily

private val DividerColor = Color(0xFFB5B5B5)


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
                    start = Offset(0f, size.height - strokeWidth / 2),
                    end = Offset(size.width, size.height - strokeWidth / 2),
                    strokeWidth = strokeWidth
                )
            },
        verticalArrangement = Arrangement.Center
    ) {
        // Label
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

        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                imageVector = Icons.Outlined.Inventory2,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.width(12.dp))

            Row(
                modifier = Modifier
                    .width(109.dp)
                    .height(35.dp)
                    .border(
                        width = 1.dp,
                        color = DividerColor,
                        shape = RoundedCornerShape(6.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepperCell(
                    label = "−",
                    onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
                    drawRightDivider = true
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = quantity.toString(),
                        style = TextStyle(
                            fontFamily = bodyFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    )
                }

                StepperCell(
                    label = "+",
                    onClick = { onQuantityChange(quantity + 1) },
                    drawLeftDivider = true
                )
            }
        }
    }
}

@Composable
private fun RowScope.StepperCell(
    label: String,
    onClick: () -> Unit,
    drawLeftDivider: Boolean = false,
    drawRightDivider: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .clickable { onClick() }
            .drawWithContent {
                drawContent()
                val strokeWidth = 1.dp.toPx()
                if (drawLeftDivider) {
                    drawLine(
                        color = DividerColor,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = strokeWidth
                    )
                }
                if (drawRightDivider) {
                    drawLine(
                        color = DividerColor,
                        start = Offset(size.width, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = strokeWidth
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.Light,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        )
    }
}