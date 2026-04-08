package cm.horion.homegaz.domain.model.auth

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.graphics.vector.ImageVector

data class AuthContext(
    val title: String = "Authentification requise",
    val description: String = "Veuillez vous connecter pour continuer.",
    val icon: ImageVector = Icons.Default.Lock
)

