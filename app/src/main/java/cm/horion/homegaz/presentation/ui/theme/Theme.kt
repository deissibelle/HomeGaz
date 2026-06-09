package cm.horion.homegaz.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cm.horion.homegaz.presentation.viewmodel.AppTheme
import cm.horion.homegaz.presentation.viewmodel.ThemeViewModel
import org.koin.compose.koinInject


data class HomeGazColors(
    // Header réservations
    val headerBg: Color,
    val headerIndicator: Color,
    val headerTextDark: Color,
    val surface: Color,

    // Background général
    val backgroundLight: Color,

    // Statut EN LIVRAISON
    val deliveringBg: Color,
    val deliveringOnBg: Color,
    val deliveringBorder: Color,

    // Statut EN ATTENTE
    val pendingBg: Color,
    val pendingOnBg: Color,
    val pendingBorder: Color,

    // Statut TERMINÉ
    val completedBg: Color,
    val completedOnBg: Color,
    val completedBorder: Color,

    // Mini-carte
    val mapBg: Color,

    // Paiement
    val orangeMoney: Color,

    // Conseils
    val advicesHeaderEconomiser: Color,
    val advicesHeaderIncendies: Color,
    val advicesHeaderQuotidien: Color,
    val advicesBodyColor: Color,
    val advicesDivider: Color,
    val advicesBackground: Color,

    // Divers
    val success: Color,
    val warning: Color,
    val divider: Color,
    val neutralGray: Color,
)


private val LightHomeGazColors = HomeGazColors(
    headerBg              = HG_Blue_Header_Bg,
    headerIndicator       = HG_Blue_Indicator,
    headerTextDark        = HG_Text_Dark_Header,
    backgroundLight       = HG_Background_Light,
    surface               = SurfaceLight,
    deliveringBg          = StatusDeliveringBg,
    deliveringOnBg        = StatusDeliveringOnBg,
    deliveringBorder      = StatusDeliveringBorder,
    pendingBg             = StatusPendingBg,
    pendingOnBg           = StatusPendingOnBg,
    pendingBorder         = StatusPendingBorder,
    completedBg           = StatusCompletedBg,
    completedOnBg         = StatusCompletedOnBg,
    completedBorder       = StatusCompletedBorder,
    mapBg                 = HG_Map_Bg,
    orangeMoney           = OrangeMoneyColor,
    advicesHeaderEconomiser = AdvicesHeaderEconomiser,
    advicesHeaderIncendies  = AdvicesHeaderIncendies,
    advicesHeaderQuotidien  = AdvicesHeaderQuotidien,
    advicesBodyColor        = AdvicesBodyColor,
    advicesDivider          = AdvicesDivider,
    advicesBackground       = AdvicesBackground,
    success               = SuccessColor,
    warning               = WarningOrange,
    divider               = DividerGray,
    neutralGray           = NeutralGray,
)

private val DarkHomeGazColors = HomeGazColors(
    headerBg              = Color(0xFF1A2F4A),
    headerIndicator       = PrimaryDark,
    headerTextDark        = Color(0xFFD5E3FF),
    backgroundLight       = Color(0xFF1E2126),
    surface               = SurfaceDark,
    deliveringBg          = Color(0xFF1B7A45),
    deliveringOnBg        = Color(0xFFFFFFFF),
    deliveringBorder      = Color(0xFF27AE60),
    pendingBg             = Color(0xFF1A5C8A),
    pendingOnBg           = Color(0xFFFFFFFF),
    pendingBorder         = Color(0xFF2980B9),
    completedBg           = Color(0xFF555C5E),
    completedOnBg         = Color(0xFFFFFFFF),
    completedBorder       = Color(0xFF6B7475),
    mapBg                 = Color(0xFF2C3340),
    orangeMoney           = Color(0xFFFF8C38),
    advicesHeaderEconomiser = Color(0xFF1B3A22),
    advicesHeaderIncendies  = Color(0xFF3A2210),
    advicesHeaderQuotidien  = Color(0xFF0E2E3A),
    advicesBodyColor        = Color(0xFF98CDF2),
    advicesDivider          = Color(0xFF2E3A45),
    advicesBackground       = Color(0xFF1A2128),
    success               = Color(0xFF34D399),
    warning               = Color(0xFFFBBF24),
    divider               = Color(0xFF2E3030),
    neutralGray           = Color(0xFF8A9389),
)

// COMPOSITION LOCALS

val LocalHomeGazColors = staticCompositionLocalOf { LightHomeGazColors }
val LocalThemeIsDark   = compositionLocalOf { false }

val MaterialTheme.homeGazColors: HomeGazColors
    @Composable
    @ReadOnlyComposable
    get() = LocalHomeGazColors.current

// MATERIAL 3 SCHEMES


private val LightColorScheme = lightColorScheme(
    primary                = PrimaryLight,
    onPrimary              = OnPrimaryLight,
    primaryContainer       = PrimaryContainerLight,
    onPrimaryContainer     = OnPrimaryContainerLight,
    secondary              = SecondaryLight,
    onSecondary            = OnSecondaryLight,
    secondaryContainer     = SecondaryContainerLight,
    onSecondaryContainer   = OnSecondaryContainerLight,
    tertiary               = TertiaryLight,
    onTertiary             = OnTertiaryLight,
    tertiaryContainer      = TertiaryContainerLight,
    onTertiaryContainer    = OnTertiaryContainerLight,
    error                  = ErrorLight,
    onError                = OnErrorLight,
    errorContainer         = ErrorContainerLight,
    onErrorContainer       = OnErrorContainerLight,
    background             = BackgroundLight,
    onBackground           = OnBackgroundLight,
    surface                = SurfaceLight,
    onSurface              = OnSurfaceLight,
    surfaceVariant         = SurfaceVariantLight,
    onSurfaceVariant       = OnSurfaceVariantLight,
    outline                = OutlineLight,
    outlineVariant         = OutlineVariantLight,
)

private val DarkColorScheme = darkColorScheme(
    primary                = PrimaryDark,
    onPrimary              = OnPrimaryDark,
    primaryContainer       = PrimaryContainerDark,
    onPrimaryContainer     = OnPrimaryContainerDark,
    secondary              = SecondaryDark,
    onSecondary            = OnSecondaryDark,
    secondaryContainer     = SecondaryContainerDark,
    onSecondaryContainer   = OnSecondaryContainerDark,
    tertiary               = TertiaryDark,
    onTertiary             = OnTertiaryDark,
    tertiaryContainer      = TertiaryContainerDark,
    onTertiaryContainer    = OnTertiaryContainerDark,
    error                  = ErrorDark,
    onError                = OnErrorDark,
    errorContainer         = ErrorContainerDark,
    onErrorContainer       = OnErrorContainerDark,
    background             = BackgroundDark,
    onBackground           = OnBackgroundDark,
    surface                = SurfaceDark,
    onSurface              = OnSurfaceDark,
    surfaceVariant         = SurfaceVariantDark,
    onSurfaceVariant       = OnSurfaceVariantDark,
    outline                = OutlineDark,
    outlineVariant         = OutlineVariantDark,
)

// THEME FUNCTION

@Composable
fun HomeGazTheme(
    content: @Composable () -> Unit
) {
    // Récupère le singleton ThemeViewModel via Koin
    val themeViewModel : ThemeViewModel = koinInject()
    val prefs          by themeViewModel.prefs.collectAsStateWithLifecycle()
    val isSystemDark    = isSystemInDarkTheme()

    // Résout le booléen isDark depuis l'enum AppTheme
    val isDark = when (prefs.theme) {
        AppTheme.LIGHT  -> false
        AppTheme.DARK   -> true
        AppTheme.SYSTEM -> isSystemDark
    }

    val colorScheme   = if (isDark) DarkColorScheme   else LightColorScheme
    val homeGazColors = if (isDark) DarkHomeGazColors  else LightHomeGazColors

    CompositionLocalProvider(
        LocalThemeIsDark   provides isDark,
        LocalHomeGazColors provides homeGazColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = AppTypography,
            content     = content,
        )
    }
}
