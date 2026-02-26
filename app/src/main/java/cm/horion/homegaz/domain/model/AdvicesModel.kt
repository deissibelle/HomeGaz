package cm.horion.homegaz.domain.model

import androidx.compose.ui.graphics.Color

data class AdviceTip(
    val emoji: String,
    val title: String,
    val bullets: List<String>
)

data class AdviceSection(
    val sectionTitle: String,
    val headerColor: Color,
    val tips: List<AdviceTip>
)