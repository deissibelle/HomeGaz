package cm.horion.homegaz.presentation.ui.components.payment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.presentation.ui.theme.bodyFontFamily
import cm.horion.homegaz.R


@Composable
fun PaymentTopBar(
    title      : String = stringResource(R.string.payment_title),
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .heightIn(min = 39.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text      = title,
            modifier  = Modifier
                .fillMaxWidth()
                .padding(start = 60.dp, end = 20.dp),
            style     = TextStyle(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 20.sp,
                lineHeight = (20 * 1.17).sp,
                color      = MaterialTheme.colorScheme.primary,
                textAlign  = TextAlign.Center
            )
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp)
                .size(width = 35.dp, height = 39.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.back),
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(100.dp)
            )
        }
    }
}