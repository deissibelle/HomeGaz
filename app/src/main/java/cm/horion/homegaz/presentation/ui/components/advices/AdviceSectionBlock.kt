package cm.horion.homegaz.presentation.ui.components.advices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.domain.model.advices.AdviceSection



@Composable
fun AdviceSectionBlock(section: AdviceSection) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        SectionHeader(
            title = section.sectionTitle,
            backgroundColor = section.headerColor
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

        ) {
            section.tips.forEachIndexed { index, tip ->
                AdviceTipCard(
                    tip = tip,
                    isLast = index == section.tips.lastIndex
                )
            }
        }
    }
}