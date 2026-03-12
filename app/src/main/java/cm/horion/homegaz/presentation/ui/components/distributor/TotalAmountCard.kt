package cm.horion.homegaz.presentation.ui.components.distributor


import androidx.compose.foundation.background
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
import cm.horion.homegaz.presentation.ui.theme.bodyFontFamily

@Composable
fun TotalAmountCard(
    total: Int,
    currency: String = "frs"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Montant total:",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = bodyFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "$total$currency",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = bodyFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}
