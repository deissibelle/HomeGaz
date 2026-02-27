package cm.horion.homegaz.presentation.ui.components.advices

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.presentation.ui.theme.PrimaryLight


@Composable
fun SectionHeader(title: String, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart    = 15.dp,
                    topEnd      = 15.dp,
                    bottomStart = 0.dp,
                    bottomEnd   = 0.dp
                )
            )
            .background(backgroundColor)
            .padding(vertical = 10.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text          = title,
            fontWeight    = FontWeight.Bold,
            fontSize      = 15.sp,
            color         = PrimaryLight,
            textAlign     = TextAlign.Center,
            letterSpacing = 0.sp
        )
    }
}
