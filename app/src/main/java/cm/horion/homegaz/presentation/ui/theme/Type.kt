package cm.horion.homegaz.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R

// Configuration du fournisseur de polices Google
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Définition de la famille de police Poppins avec ses différentes graisses
var poppinsFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Poppins"),
        fontProvider = provider,
        weight = FontWeight.Normal
    ),
    Font(
        googleFont = GoogleFont("Poppins"),
        fontProvider = provider,
        weight = FontWeight.Medium
    ),
    Font(
        googleFont = GoogleFont("Poppins"),
        fontProvider = provider,
        weight = FontWeight.SemiBold
    ),
    Font(
        googleFont = GoogleFont("Poppins"),
        fontProvider = provider,
        weight = FontWeight.Bold
    )
)

// Configuration de la typographie pour l'application
val AppTypography = Typography(
    // Style pour le texte de corps
    bodyLarge = TextStyle(
        fontFamily = poppinsFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    // Style pour les grands titres
    titleLarge = TextStyle(
        fontFamily = poppinsFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    // Style pour les petits labels ou légendes
    labelSmall = TextStyle(
        fontFamily = poppinsFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 0.sp,
        letterSpacing = 0.sp
    )
)
