package cm.horion.homegaz.presentation.ui.advices

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.AdviceSection
import cm.horion.homegaz.domain.model.AdviceTip
import cm.horion.homegaz.presentation.ui.theme.AdvicesBackground
import cm.horion.homegaz.presentation.ui.theme.AdvicesBodyColor
import cm.horion.homegaz.presentation.ui.theme.AdvicesDivider
import cm.horion.homegaz.presentation.ui.theme.AdvicesHeaderEconomiser
import cm.horion.homegaz.presentation.ui.theme.AdvicesHeaderIncendies
import cm.horion.homegaz.presentation.ui.theme.AdvicesHeaderQuotidien
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


@Composable
fun AdviceSectionBlock(section: AdviceSection) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        SectionHeader(
            title           = section.sectionTitle,
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
                .background(Color.White)
        ) {
            section.tips.forEachIndexed { index, tip ->
                AdviceTipCard(
                    tip    = tip,
                    isLast = index == section.tips.lastIndex
                )
            }
        }
    }
}


@Composable
fun DailyPracticesBlock(title: String, practices: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        SectionHeader(
            title           = title,
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
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            practices.forEachIndexed { index, practice ->
                Text(
                    text       = "${index + 1}. $practice",
                    fontWeight = FontWeight.Normal,
                    fontSize   = 14.sp,
                    lineHeight = 22.sp,
                    color      = AdvicesBodyColor
                )
            }
        }
    }
}



@Composable
fun AdvicesScreen() {

    val adviceSections = listOf(
        AdviceSection(
            sectionTitle = stringResource(R.string.advices_section_economiser),
            headerColor  = AdvicesHeaderEconomiser,
            tips = listOf(
                AdviceTip(
                    emoji   = "🍲",
                    title   = stringResource(R.string.advices_eco_tip1_title),
                    bullets = listOf(
                        stringResource(R.string.advices_eco_tip1_b1),
                        stringResource(R.string.advices_eco_tip1_b2),
                        stringResource(R.string.advices_eco_tip1_b3)
                    )
                ),
                AdviceTip(
                    emoji   = "🍳",
                    title   = stringResource(R.string.advices_eco_tip2_title),
                    bullets = listOf(
                        stringResource(R.string.advices_eco_tip2_b1),
                        stringResource(R.string.advices_eco_tip2_b2),
                        stringResource(R.string.advices_eco_tip2_b3)
                    )
                ),
                AdviceTip(
                    emoji   = "🍛",
                    title   = stringResource(R.string.advices_eco_tip3_title),
                    bullets = listOf(
                        stringResource(R.string.advices_eco_tip3_b1),
                        stringResource(R.string.advices_eco_tip3_b2),
                        stringResource(R.string.advices_eco_tip3_b3)
                    )
                ),
                AdviceTip(
                    emoji   = "🥘",
                    title   = stringResource(R.string.advices_eco_tip4_title),
                    bullets = listOf(
                        stringResource(R.string.advices_eco_tip4_b1),
                        stringResource(R.string.advices_eco_tip4_b2),
                        stringResource(R.string.advices_eco_tip4_b3)
                    )
                ),
                AdviceTip(
                    emoji   = "🔄",
                    title   = stringResource(R.string.advices_eco_tip5_title),
                    bullets = listOf(
                        stringResource(R.string.advices_eco_tip5_b1),
                        stringResource(R.string.advices_eco_tip5_b2),
                        stringResource(R.string.advices_eco_tip5_b3),
                        stringResource(R.string.advices_eco_tip5_b4)
                    )
                )
            )
        ),
        AdviceSection(
            sectionTitle = stringResource(R.string.advices_section_incendies),
            headerColor  = AdvicesHeaderIncendies,
            tips = listOf(
                AdviceTip(
                    emoji   = "🚨",
                    title   = stringResource(R.string.advices_inc_tip1_title),
                    bullets = listOf(
                        stringResource(R.string.advices_inc_tip1_b1),
                        stringResource(R.string.advices_inc_tip1_b2),
                        stringResource(R.string.advices_inc_tip1_b3),
                        stringResource(R.string.advices_inc_tip1_b4)
                    )
                ),
                AdviceTip(
                    emoji   = "🔧",
                    title   = stringResource(R.string.advices_inc_tip2_title),
                    bullets = listOf(
                        stringResource(R.string.advices_inc_tip2_b1),
                        stringResource(R.string.advices_inc_tip2_b2),
                        stringResource(R.string.advices_inc_tip2_b3),
                        stringResource(R.string.advices_inc_tip2_b4)
                    )
                ),
                AdviceTip(
                    emoji   = "🔥",
                    title   = stringResource(R.string.advices_inc_tip3_title),
                    bullets = listOf(
                        stringResource(R.string.advices_inc_tip3_b1),
                        stringResource(R.string.advices_inc_tip3_b2),
                        stringResource(R.string.advices_inc_tip3_b3),
                        stringResource(R.string.advices_inc_tip3_b4),
                        stringResource(R.string.advices_inc_tip3_b5),
                        stringResource(R.string.advices_inc_tip3_b6)
                    )
                ),
                AdviceTip(
                    emoji   = "🚫",
                    title   = stringResource(R.string.advices_inc_tip4_title),
                    bullets = listOf(
                        stringResource(R.string.advices_inc_tip4_b1),
                        stringResource(R.string.advices_inc_tip4_b2),
                        stringResource(R.string.advices_inc_tip4_b3),
                        stringResource(R.string.advices_inc_tip4_b4)
                    )
                ),
                AdviceTip(
                    emoji   = "🔒",
                    title   = stringResource(R.string.advices_inc_tip5_title),
                    bullets = listOf(
                        stringResource(R.string.advices_inc_tip5_b1)
                    )
                )
            )
        )
    )

    val dailyPractices = listOf(
        stringResource(R.string.advices_daily_1),
        stringResource(R.string.advices_daily_2),
        stringResource(R.string.advices_daily_3),
        stringResource(R.string.advices_daily_4),
        stringResource(R.string.advices_daily_5)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AdvicesBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        adviceSections.forEach { section ->
            AdviceSectionBlock(section = section)
        }
        DailyPracticesBlock(
            title     = stringResource(R.string.advices_section_quotidien),
            practices = dailyPractices
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}