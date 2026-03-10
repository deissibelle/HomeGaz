package cm.horion.homegaz.presentation.ui.pages.advices

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.AdviceSection
import cm.horion.homegaz.domain.model.AdviceTip
import cm.horion.homegaz.presentation.ui.theme.AdvicesBackground
import cm.horion.homegaz.presentation.ui.theme.AdvicesHeaderEconomiser
import cm.horion.homegaz.presentation.ui.theme.AdvicesHeaderIncendies
import cm.horion.homegaz.presentation.ui.components.advices.DailyPracticesBlock
import cm.horion.homegaz.presentation.ui.components.advices.AdviceSectionBlock


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