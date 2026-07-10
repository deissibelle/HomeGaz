package cm.horion.homegaz.presentation.ui.components.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun SsoLoadingDialog() {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false, // Empêche d'annuler en plein échange de tokens
            dismissOnClickOutside = false
        )
    ) {
        // Le petit carré au design soigné
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp) // Taille du carré
                .background(
                    color = Color("#0F172A".toColorInt()), // Fond Slate sombre assorti à ton thème
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // L'indicateur de chargement turquoise/bleu
                CircularProgressIndicator(
                    color = Color("#38BDF8".toColorInt()), // Couleur d'accentuation
                    modifier = Modifier.size(40.dp),
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Connexion...",
                    color = Color.White,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = androidx.compose.ui.unit.TextUnit.Unspecified
                    )
                )
            }
        }
    }
}

// Helper pour parser la couleur hexadécimale simplement
fun String.toColorInt(): Int = android.graphics.Color.parseColor(this)