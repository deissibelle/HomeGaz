package cm.horion.homegaz.presentation.ui.components.advices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.domain.model.advices.AdviceTip
import cm.horion.homegaz.presentation.ui.theme.AdvicesBodyColor
import cm.horion.homegaz.presentation.ui.theme.AdvicesDivider
import cm.horion.homegaz.presentation.ui.theme.PrimaryLight


@Composable
fun AdviceTipCard(tip: AdviceTip, isLast: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text       = "${tip.emoji}  ${tip.title}",
            fontWeight = FontWeight.Bold,
            fontSize   = 15.sp,
            color      = PrimaryLight,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Bullets
        tip.bullets.forEach { bullet ->
            val isIndented = bullet.startsWith("a)") ||
                    bullet.startsWith("b)") ||
                    bullet.startsWith("→")  ||
                    bullet.startsWith("Si tu sens")

            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 3.dp),
                verticalAlignment = Alignment.Top
            ) {
                if (!isIndented) {
                    Text(
                        text       = "• ",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 14.sp,
                        color      = AdvicesBodyColor
                    )
                } else {
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text       = bullet,
                    fontWeight = FontWeight.Normal,
                    fontSize   = 14.sp,
                    lineHeight = 20.sp,
                    color      = AdvicesBodyColor,
                    modifier   = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (!isLast) {
        HorizontalDivider(
            modifier  = Modifier.padding(horizontal = 12.dp),
            thickness = 1.dp,
            color     = AdvicesDivider
        )
    }
}

