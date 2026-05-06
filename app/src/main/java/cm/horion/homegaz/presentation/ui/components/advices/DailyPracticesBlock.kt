package cm.horion.homegaz.presentation.ui.components.advices

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.presentation.ui.theme.AdvicesBodyColor
import cm.horion.homegaz.presentation.ui.theme.AdvicesHeaderQuotidien


@Composable
fun DailyPracticesBlock(title: String, practices: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        SectionHeader(
            title = title,
            backgroundColor = AdvicesHeaderQuotidien
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        topStart    = 0.dp,
                        topEnd      = 0.dp,
                        bottomStart = 12.dp,
                        bottomEnd   = 12.dp
                    )
                )
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            practices.forEachIndexed { index, practice ->
                Text(
                    text       = "${index + 1}. $practice",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        letterSpacing = 0.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Normal,

                        ),
                )
            }
        }
    }
}