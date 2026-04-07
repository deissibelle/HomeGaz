package cm.horion.homegaz.presentation.ui.components.gazprofile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.presentation.state.GazProfileUiState

private val GAZ_BRANDS = listOf(
    "Total", "Tradex", "SCTM", "Liquigaz", "Camgaz", "Mviwa", "Autre"
)

@Composable
fun GazProfileFormCard(
    uiState          : GazProfileUiState,
    onCapacityChange : (String) -> Unit,
    onBrandChange    : (String) -> Unit,
    onLocationChange : (String) -> Unit,
) {
    val capacityOptions = listOf(
        stringResource(R.string.gaz_profile_capacity_6),
        stringResource(R.string.gaz_profile_capacity_12),
        stringResource(R.string.gaz_profile_capacity_38)
    )

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(400)) + slideInVertically(
            animationSpec  = tween(400, easing = EaseOutCubic),
            initialOffsetY = { it / 6 }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(
                    elevation    = 4.dp,
                    shape        = RoundedCornerShape(24.dp),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    spotColor    = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            FormSection(
                stepNumber = 1,
                title      = stringResource(R.string.gaz_profile_capacity),
                isFilled   = uiState.capacityKg.isNotBlank()
            ) {
                CapacityChipSelector(
                    options  = capacityOptions,
                    selected = uiState.capacityKg,
                    onSelect = onCapacityChange
                )
            }

            FormDivider()

            FormSection(
                stepNumber = 2,
                title      = stringResource(R.string.gaz_profile_brand),
                isFilled   = uiState.brand.isNotBlank()
            ) {
                GazProfileDropdownField(
                    icon     = Icons.Outlined.LocalOffer,
                    label    = "",
                    options  = GAZ_BRANDS,
                    selected = uiState.brand,
                    onSelect = onBrandChange
                )
            }

            FormDivider()

            FormSection(
                stepNumber = 3,
                title      = stringResource(R.string.gaz_profile_location),
                isFilled   = uiState.usageLocation.isNotBlank()
            ) {
                GazProfileLocationField(
                    value         = uiState.usageLocation,
                    onValueChange = onLocationChange,
                    showLabel     = false
                )
            }
        }
    }
}


@Composable
private fun FormDivider() {
    HorizontalDivider(
        modifier  = Modifier.padding(vertical = 20.dp),
        thickness = 0.8.dp,
        color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}


@Composable
private fun FormSection(
    stepNumber : Int,
    title      : String,
    isFilled   : Boolean,
    content    : @Composable () -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment     = Alignment.Top
    ) {
        StepBadge(number = stepNumber, completed = isFilled)

        Column(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (title.isNotBlank()) {
                Text(
                    text       = title,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onBackground,
                    fontSize   = 14.sp
                )
            }
            content()
        }
    }
}


@Composable
private fun StepBadge(number: Int, completed: Boolean) {
    val bgColor by animateColorAsState(
        targetValue   = if (completed) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.primaryContainer,
        animationSpec = tween(300),
        label         = "badge_bg"
    )
    val textColor by animateColorAsState(
        targetValue   = if (completed) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.primary,
        animationSpec = tween(300),
        label         = "badge_text"
    )
    val scale by animateFloatAsState(
        targetValue   = if (completed) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "badge_scale"
    )

    Box(
        modifier = Modifier
            .padding(top = 2.dp)
            .size((32 * scale).dp)
            .background(color = bgColor, shape = RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState   = completed,
            transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
            label          = "badge_content"
        ) { done ->
            if (done) {
                Icon(
                    imageVector        = Icons.Outlined.Check,
                    contentDescription = null,
                    tint               = textColor,
                    modifier           = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text       = number.toString(),
                    color      = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 13.sp
                )
            }
        }
    }
}

// ─── Chips de capacité ────────────────────────────────────────────────────────

@Composable
private fun CapacityChipSelector(
    options  : List<String>,
    selected : String,
    onSelect : (String) -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected

            val bgColor by animateColorAsState(
                targetValue   = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface,
                animationSpec = tween(250),
                label         = "chip_bg_$option"
            )
            val textColor by animateColorAsState(
                targetValue   = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(250),
                label         = "chip_text_$option"
            )
            val borderColor by animateColorAsState(
                targetValue   = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outlineVariant,
                animationSpec = tween(250),
                label         = "chip_border_$option"
            )

            Surface(
                onClick  = { onSelect(option) },
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(12.dp)
                    ),
                color = bgColor,
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier         = Modifier.padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = option,
                        color      = textColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize   = 13.sp
                    )
                }
            }
        }
    }
}