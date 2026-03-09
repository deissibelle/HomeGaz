package cm.horion.homegaz.presentation.ui.components.distributor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.presentation.ui.theme.bodyFontFamily

@Composable
fun DistributorHeader(
    title: String = "Algo Gaz",
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(222.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.algogaz),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(189.dp),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .padding(top = 77.dp, start = 20.dp)
                .size(width = 35.dp, height = 39.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.2f))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Retour",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 153.dp, start = 20.dp)
                .size(69.dp)
                .clip(CircleShape)
                .background(Color(0xFFE31E24))
                .border(3.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.distributor_logo),
                contentDescription = null,
                modifier = Modifier.size(45.dp),
                contentScale = ContentScale.Fit
            )
        }


        Text(
            text = title,
            modifier = Modifier
                .padding(top = 189.dp, start = 97.dp),
            style = TextStyle(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                lineHeight = 23.4.sp
            )
        )
    }
}
