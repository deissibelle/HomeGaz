package cm.horion.homegaz.utils


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object ThemeColor {
    val Primary : Color
        @Composable get() = MaterialTheme.colorScheme.primary
    val BackgroundLight : Color
        @Composable get() = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
    val Outline : Color
        @Composable get() = MaterialTheme.colorScheme.outline
    val BackgroundDark = Color(0xFF111621)
    val CardBorder = Color(0xFFE2E8F0)
    val SubtitleColor = Color(0xFF7E7E7E)
    val ButtonShadowColor = Color(0xFF00D5E1)
    val IconBackgroundColor = Color(0xFFB9ECEE)
    val TextPrimary : Color
        @Composable get() = MaterialTheme.colorScheme.onBackground
    val TextSecondary : Color
        @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
    val Success = Color(0xFF10B981)
}