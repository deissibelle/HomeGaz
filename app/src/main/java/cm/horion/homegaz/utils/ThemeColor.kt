package cm.horion.homegaz.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import cm.horion.homegaz.presentation.ui.theme.SuccessColor

object ThemeColor {
    val Primary: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary

    val Background: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)

    val Outline: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline

    val CardBorder: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outlineVariant

    val TextPrimary: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onBackground

    val TextSecondary: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant

    val IconBackground: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.secondaryContainer

    val Success = SuccessColor
}
