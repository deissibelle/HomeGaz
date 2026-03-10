package cm.horion.homegaz.presentation.ui.components.payment


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.PaymentMethod
import cm.horion.homegaz.presentation.ui.components.common.OptionSelectorRow


@Composable
fun PaymentOptionRow(
    selectedMethod: PaymentMethod,
    onMethodSelected: (PaymentMethod) -> Unit
) {
    val options = listOf("Orange Money", "MoMo MTN")
    val logos = listOf(R.drawable.orange_money, R.drawable.momo)
    val selectedIndex = if (selectedMethod == PaymentMethod.ORANGE_MONEY) 0 else 1

    OptionSelectorRow(
        label = "Option",
        options = options,
        selectedIndex = selectedIndex,
        onSelect = { index ->
            onMethodSelected(if (index == 0) PaymentMethod.ORANGE_MONEY else PaymentMethod.MOMO)
        },
        radioSize = 20.dp,
        cardHeight = 48.dp,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 0.3.dp.toPx()
                drawLine(
                    color = Color(0xFF9E9D9D),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            }
            .padding(bottom = 10.dp),
        content = { index, _ ->
            Image(
                painter = painterResource(id = logos[index]),
                contentDescription = options[index],
                modifier = Modifier
                    .width(114.dp)
                    .height(36.dp),
                contentScale = ContentScale.Fit,
                alignment = Alignment.CenterStart
            )
        }
    )
}