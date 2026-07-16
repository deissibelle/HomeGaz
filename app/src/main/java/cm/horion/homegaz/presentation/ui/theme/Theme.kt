package cm.horion.homegaz.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cm.horion.homegaz.presentation.viewmodel.AppTheme
import cm.horion.homegaz.presentation.viewmodel.ThemeViewModel
import org.koin.compose.koinInject
import android.app.Activity
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


data class HomeGazColors(
    val headerBg: Color,
    val headerIndicator: Color,
    val headerTextDark: Color,
    val surface: Color,
    val backgroundLight: Color,

    // Statuts de livraison
    val deliveringBg: Color,
    val deliveringOnBg: Color,
    val deliveringBorder: Color,

    val pendingBg: Color,
    val pendingOnBg: Color,
    val pendingBorder: Color,

    val completedBg: Color,
    val completedOnBg: Color,
    val completedBorder: Color,

    val mapBg: Color,
    val orangeMoney: Color,

    // Conseils
    val advicesHeaderEconomiser: Color,
    val advicesHeaderIncendies: Color,
    val advicesHeaderQuotidien: Color,
    val advicesBodyColor: Color,
    val advicesDivider: Color,
    val advicesBackground: Color,

    val success: Color,
    val warning: Color,
    val divider: Color,
    val neutralGray: Color,
)

// ─────────────────────────────────────────────
// PALETTES SPECIFIQUES HOMEGAZ (Clair / Sombre)
// ─────────────────────────────────────────────
private val LightHomeGazColors = HomeGazColors(
    headerBg                = PrimaryContainerLight, // Aligné sur le container bleu clair du thème
    headerIndicator         = PrimaryLight,          // Aligné sur le bleu primaire de marque
    headerTextDark          = OnPrimaryContainerLight, // Aligné sur le texte lisible du container
    backgroundLight         = BackgroundLight,       // Strictement identique à la couleur de fond
    surface                 = SurfaceLight,           // Strictement identique à la couleur de surface

    // Statut EN LIVRAISON (Vert)
    deliveringBg            = SuccessColor.copy(alpha = 0.15f), // Fond vert translucide doux
    deliveringOnBg          = SuccessColor,                     // Texte vert vif
    deliveringBorder        = SuccessColor.copy(alpha = 0.4f),  // Bordure verte discrète

    // Statut EN ATTENTE (Orange/Alerte)
    pendingBg               = WarningOrange.copy(alpha = 0.15f), // Fond orange translucide doux
    pendingOnBg             = WarningOrange,                     // Texte orange
    pendingBorder           = WarningOrange.copy(alpha = 0.4f),  // Bordure orange discrète

    // Statut TERMINÉ (Gris)
    completedBg             = NeutralGray.copy(alpha = 0.15f),   // Fond gris translucide doux
    completedOnBg           = NeutralGray,                       // Texte gris
    completedBorder         = NeutralGray.copy(alpha = 0.4f),    // Bordure grise discrète

    mapBg                   = SurfaceVariantLight,   // Aligné sur le gris de second plan
    orangeMoney             = OrangeMoneyColor,      // Couleur sémantique du paiement

    // Conseils (Harmonisation douce)
    advicesHeaderEconomiser = Color(0xFFD9FFE3),
    advicesHeaderIncendies  = Color(0xFFFFE9D9),
    advicesHeaderQuotidien  = Color(0xFFD9F4FF),
    advicesBodyColor        = PrimaryLight,          // Aligné sur le bleu de texte de marque
    advicesDivider          = OutlineVariantLight,   // Aligné sur le diviseur discret du thème
    advicesBackground       = SurfaceVariantLight,   // Aligné sur le gris de fond de carte du thème

    success                 = SuccessColor,
    warning                 = WarningOrange,
    divider                 = OutlineVariantLight,   // Strictement identique au diviseur du ColorScheme
    neutralGray             = OutlineLight,          // Strictement identique au gris système
)

private val DarkHomeGazColors = HomeGazColors(
    headerBg                = PrimaryContainerDark,  // Aligné sur le container sombre du thème
    headerIndicator         = PrimaryDark,           // Aligné sur le bleu primaire du mode sombre
    headerTextDark          = OnPrimaryContainerDark, // Aligné sur le texte lisible du container sombre
    backgroundLight         = BackgroundDark,        // Strictement identique au fond noir de l'appli
    surface                 = SurfaceDark,           // Strictement identique à la surface noire pure

    // Statut EN LIVRAISON (Vert optimisé Dark)
    deliveringBg            = SuccessColor.copy(alpha = 0.2f),
    deliveringOnBg          = SuccessColor,
    deliveringBorder        = SuccessColor.copy(alpha = 0.4f),

    // Statut EN ATTENTE (Orange optimisé Dark)
    pendingBg               = WarningOrange.copy(alpha = 0.2f),
    pendingOnBg             = WarningOrange,
    pendingBorder           = WarningOrange.copy(alpha = 0.4f),

    // Statut TERMINÉ (Gris optimisé Dark)
    completedBg             = NeutralGray.copy(alpha = 0.2f),
    completedOnBg           = NeutralGray,
    completedBorder         = NeutralGray.copy(alpha = 0.4f),

    mapBg                   = SurfaceVariantDark,    // Aligné sur le gris carbone des cartes
    orangeMoney             = OrangeMoneyColor,      // Couleur sémantique du paiement

    // Conseils (Adaptés pour l'obscurité pour éviter de fatiguer les yeux)
    advicesHeaderEconomiser = Color(0xFF1B3A22),
    advicesHeaderIncendies  = Color(0xFF3A2210),
    advicesHeaderQuotidien  = Color(0xFF0E2E3A),
    advicesBodyColor        = SecondaryDark,         // Texte lisible sur fond sombre
    advicesDivider          = OutlineVariantDark,    // Aligné sur le diviseur discret du thème sombre
    advicesBackground       = SurfaceVariantDark,    // Aligné sur le gris de fond de carte du thème sombre

    success                 = SuccessColor,
    warning                 = WarningOrange,
    divider                 = OutlineVariantDark,    // Strictement identique au diviseur du ColorScheme sombre
    neutralGray             = OutlineDark,           // Strictement identique au gris système sombre
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

@Composable
fun HomeGazTheme(
    content: @Composable () -> Unit
) {
    val themeViewModel : ThemeViewModel = koinInject()
    val prefs          by themeViewModel.prefs.collectAsStateWithLifecycle()
    val isSystemDark    = isSystemInDarkTheme()

    val isDark = when (prefs.theme) {
        AppTheme.LIGHT  -> false
        AppTheme.DARK   -> true
        AppTheme.SYSTEM -> isSystemDark
    }

    val colorScheme   = if (isDark) DarkColorScheme   else LightColorScheme
    //val homeGazColors = if (isDark) DarkColorScheme  else LightColorScheme
    val homeGazColors = if (isDark) DarkHomeGazColors  else LightHomeGazColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()

//            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !isDark
            insetsController.isAppearanceLightNavigationBars = !isDark
        }
    }

    CompositionLocalProvider(
        LocalThemeIsDark  provides isDark,
        LocalHomeGazColors provides homeGazColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = AppTypography,
            content     = { Surface(content = content) },
        )
    }
}
