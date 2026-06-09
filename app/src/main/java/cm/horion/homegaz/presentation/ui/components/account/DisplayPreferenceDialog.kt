package cm.horion.homegaz.presentation.ui.components.account

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cm.horion.homegaz.presentation.viewmodel.AppLanguage
import cm.horion.homegaz.presentation.viewmodel.AppTheme
import cm.horion.homegaz.presentation.viewmodel.DisplayPreferences

/**
 * Popup "Préférences d'affichage" :
 *  - Thème (ouvre un second dialog)
 *  - Langue
 */
@Composable
fun DisplayPreferencesDialog(
    prefs        : DisplayPreferences,
    onThemeChange: (AppTheme) -> Unit,
    onLangChange : (AppLanguage) -> Unit,
    onDismiss    : () -> Unit
) {
    var showThemePicker by remember { mutableStateOf(false) }

    // ── Dialog principal ──────────────────────────────────────────────────────
    Dialog(
        onDismissRequest = onDismiss,
        properties       = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier  = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape     = RoundedCornerShape(24.dp),
            color     = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                // Titre
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Outlined.SettingsSuggest,
                            contentDescription = null,
                            tint               = MaterialTheme.colorScheme.primary,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text       = "Préférences d'affichage",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text  = "Personnalisez votre expérience",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── Thème ─────────────────────────────────────────────────────
                PreferenceRow(
                    icon        = themeIcon(prefs.theme),
                    label       = "Thème",
                    valueLabel  = prefs.theme.label(),
                    onClick     = { showThemePicker = true }
                )

                HorizontalDivider(
                    modifier  = Modifier.padding(vertical = 8.dp),
                    thickness = 0.5.dp,
                    color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // ── Langue ────────────────────────────────────────────────────
                Text(
                    text       = "Langue",
                    style      = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier   = Modifier.padding(bottom = 10.dp)
                )
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        LanguageChip(
                            language   = lang,
                            isSelected = prefs.language == lang,
                            onClick    = { onLangChange(lang) },
                            modifier   = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Bouton fermer
                Button(
                    onClick  = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Text("Fermer", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    // ── Dialog sélection du thème ─────────────────────────────────────────────
    if (showThemePicker) {
        ThemePickerDialog(
            current   = prefs.theme,
            onSelect  = { theme ->
                onThemeChange(theme)
                showThemePicker = false
            },
            onDismiss = { showThemePicker = false }
        )
    }
}


// ─── Dialog sélection du thème ────────────────────────────────────────────────

@Composable
fun ThemePickerDialog(
    current   : AppTheme,
    onSelect  : (AppTheme) -> Unit,
    onDismiss : () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties       = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier       = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            shape          = RoundedCornerShape(24.dp),
            color          = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                Text(
                    text       = "Choisir le thème",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.padding(bottom = 20.dp)
                )

                AppTheme.entries.forEach { theme ->
                    ThemeOptionRow(
                        theme      = theme,
                        isSelected = theme == current,
                        onClick    = { onSelect(theme) }
                    )
                    if (theme != AppTheme.entries.last()) {
                        HorizontalDivider(
                            modifier  = Modifier.padding(vertical = 4.dp),
                            thickness = 0.5.dp,
                            color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                TextButton(
                    onClick  = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Annuler")
                }
            }
        }
    }
}


// ─── Composants internes ──────────────────────────────────────────────────────

@Composable
private fun PreferenceRow(
    icon       : ImageVector,
    label      : String,
    valueLabel : String,
    onClick    : () -> Unit
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.primary,
            modifier           = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text      = label,
            style     = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier  = Modifier.weight(1f)
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text     = valueLabel,
                style    = MaterialTheme.typography.labelSmall,
                color    = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
        Spacer(Modifier.width(6.dp))
        Icon(
            imageVector        = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.outlineVariant,
            modifier           = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun ThemeOptionRow(
    theme      : AppTheme,
    isSelected : Boolean,
    onClick    : () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue   = if (isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surface,
        animationSpec = tween(200),
        label         = "theme_row_bg"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icône du thème
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = themeIcon(theme),
                contentDescription = null,
                tint               = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = theme.label(),
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color      = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onBackground
            )
            Text(
                text  = themeDescription(theme),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Radio button
        AnimatedVisibility(
            visible = isSelected,
            enter   = fadeIn(tween(150)) + scaleIn(tween(150)),
            exit    = fadeOut(tween(150)) + scaleOut(tween(150))
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(4.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
        if (!isSelected) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            )
        }
    }
}

@Composable
private fun LanguageChip(
    language   : AppLanguage,
    isSelected : Boolean,
    onClick    : () -> Unit,
    modifier   : Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue   = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(200),
        label         = "lang_chip_bg"
    )
    val textColor by animateColorAsState(
        targetValue   = if (isSelected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label         = "lang_chip_text"
    )

    Surface(
        onClick  = onClick,
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            ),
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier             = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text       = languageFlag(language),
                fontSize   = 18.sp
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text       = language.label(),
                style      = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color      = textColor
            )
        }
    }
}


// ─── Helpers ──────────────────────────────────────────────────────────────────

fun themeIcon(theme: AppTheme): ImageVector = when (theme) {
    AppTheme.LIGHT  -> Icons.Outlined.LightMode
    AppTheme.DARK   -> Icons.Outlined.DarkMode
    AppTheme.SYSTEM -> Icons.Outlined.Contrast
}

private fun themeDescription(theme: AppTheme): String = when (theme) {
    AppTheme.LIGHT  -> "Toujours en mode clair"
    AppTheme.DARK   -> "Toujours en mode sombre"
    AppTheme.SYSTEM -> "Suit les réglages du téléphone"
}

private fun languageFlag(lang: AppLanguage): String = when (lang) {
    AppLanguage.FRENCH  -> "🇫🇷"
    AppLanguage.ENGLISH -> "🇬🇧"
}