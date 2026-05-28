package cm.horion.homegaz.presentation.ui.components.confirmation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.distributor.DeliveryOption
import cm.horion.homegaz.domain.model.distributor.OrderSummary
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.presentation.ui.components.distributor.ProductInfoRow
import cm.horion.homegaz.presentation.ui.theme.poppinsFontFamily

@Composable
fun OrderRecapCard(summary: OrderSummary) {
    val borderColor = MaterialTheme.colorScheme.outlineVariant

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
                    color = borderColor,
                    style = Stroke(
                        width      = stroke,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                )
            }
            .padding(vertical = 4.dp)
    ) {
        ProductInfoRow(
            label = stringResource(R.string.label_brand),
            value = summary.brand,
            icon = Icons.Outlined.Settings
        )
        ProductInfoRow(
            label = stringResource(R.string.label_weight),
            value = summary.weight,
            icon = Icons.Outlined.Scale
        )
        ProductInfoRow(
            label = stringResource(R.string.label_quantity),
            value = summary.quantity.toString(),
            icon = Icons.Outlined.Inventory2
        )

        val optionLabel = if (summary.deliveryOption == DeliveryOption.LIVRAISON)
            stringResource(R.string.delivery_option_delivery)
        else
            stringResource(R.string.delivery_option_pickup)

        ProductInfoRow(
            label = stringResource(R.string.label_option),
            value = optionLabel,
            icon = Icons.Outlined.LocalShipping
        )

        PaymentMethodRow(method = summary.paymentMethod, phone = summary.phoneNumber)
    }
}

@Composable
private fun PaymentMethodRow(method: PaymentMethod, phone: String) {
    val methodLabel = when (method) {
        PaymentMethod.OM -> stringResource(R.string.payment_method_om)
        PaymentMethod.MOMO         -> stringResource(R.string.payment_method_momo)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text  = stringResource(R.string.label_payment_method),
            style = TextStyle(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Light,
                fontSize   = 13.sp,
                color      = Color(0xFFAFB0B1)
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

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
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp,
                    color      = MaterialTheme.colorScheme.primary
                )
            )

            if (phone.isNotBlank()) {
                Text(
                    text  = "  |  $phone",
                    style = TextStyle(
                        fontFamily =poppinsFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 16.sp,
                        color      = Color(0xFFAFB0B1)
                    )
                )
            }
        }
    }
}