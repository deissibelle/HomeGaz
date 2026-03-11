package cm.horion.homegaz.presentation.ui.components.confirmation


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.domain.model.DeliveryOption
import cm.horion.homegaz.domain.model.OrderSummary
import cm.horion.homegaz.domain.model.PaymentMethod
import cm.horion.homegaz.presentation.ui.components.distributor.ProductInfoRow
import cm.horion.homegaz.presentation.ui.theme.bodyFontFamily

private val DashedBorderColor = Color(0xFFB5B5B5)


@Composable
fun OrderRecapCard(summary: OrderSummary) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 11.dp)
            .drawBehind {
                val stroke = 1.dp.toPx()
                val radius = 8.dp.toPx()
                val path = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left         = stroke / 2,
                            top          = stroke / 2,
                            right        = size.width - stroke / 2,
                            bottom       = size.height - stroke / 2,
                            cornerRadius = CornerRadius(radius)
                        )
                    )
                }
                drawPath(
                    path  = path,
                    color = DashedBorderColor,
                    style = Stroke(
                        width      = stroke,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                )
            }
            .padding(vertical = 4.dp)
    ) {
        ProductInfoRow(label = "Marque",   value = summary.brand,               icon = Icons.Outlined.Settings)
        ProductInfoRow(label = "Poids",    value = summary.weight,              icon = Icons.Outlined.Scale)
        ProductInfoRow(label = "Quantité", value = summary.quantity.toString(), icon = Icons.Outlined.Inventory2)
        ProductInfoRow(
            label = "Option",
            value = if (summary.deliveryOption == DeliveryOption.LIVRAISON) "Livraison" else "Retrait",
            icon  = Icons.Outlined.LocalShipping
        )

        PaymentMethodRow(method = summary.paymentMethod, phone = summary.phoneNumber)
    }
}

@Composable
private fun PaymentMethodRow(method: PaymentMethod, phone: String) {
    val methodLabel = when (method) {
        PaymentMethod.ORANGE_MONEY -> "Orange Money"
        PaymentMethod.MOMO         -> "MoMo MTN"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text  = "Mode de paiement",
            style = TextStyle(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.Light,
                fontSize   = 13.sp,
                color      = Color(0xFFAFB0B1)
            )
        )

        Spacer(modifier = Modifier.height(2.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector        = Icons.Outlined.Payment,
                contentDescription = null,
                modifier           = Modifier.size(16.dp),
                tint               = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text  = methodLabel,
                style = TextStyle(
                    fontFamily = bodyFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp,
                    color      = MaterialTheme.colorScheme.primary
                )
            )

            if (phone.isNotBlank()) {
                Text(
                    text  = "  |  $phone",
                    style = TextStyle(
                        fontFamily = bodyFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 16.sp,
                        color      = Color(0xFFAFB0B1)
                    )
                )
            }
        }
    }
}