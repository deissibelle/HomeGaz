package cm.horion.homegaz.presentation.ui.pages.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.auth.AuthContext
import cm.horion.homegaz.domain.model.common.Screen
import cm.horion.homegaz.presentation.ui.pages.auth.AuthGuardScreen
import coil.compose.AsyncImage

@Composable
fun AccountScreen(navController: NavController) {
    val isLoggedIn by remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        AuthGuardScreen(
            authContext = AuthContext(
                title = stringResource(R.string.auth_title_account),
                description = stringResource(R.string.auth_desc_account),
                icon = Icons.Outlined.Person
            ),
            onLoginClick = { /* navController.navigate(Screen.Login.route) */ },
            onRegisterClick = { /* navController.navigate(Screen.Register.route) */ },
            onForgotPasswordClick = { /* ... */ }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeaderSection()

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                AccountSectionTitle(title = "Ma bouteille")
                AccountMenuItem(
                    icon = Icons.Outlined.LocalFireDepartment,
                    label = "Mon Profil Gaz",
                    sublabel = "Capacité, marque, consommation…",
                    onClick = { navController.navigate(Screen.GazProfile.route) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                AccountSectionTitle(title = "Paramètres")
                AccountMenuItem(
                    icon = Icons.Outlined.Notifications,
                    label = "Notifications",
                    sublabel = "Gérer les alertes et rappels",
                    onClick = { }
                )
                AccountMenuItem(
                    icon = Icons.Outlined.DarkMode,
                    label = "Thème",
                    sublabel = "Clair / Sombre",
                    onClick = { }
                )
                AccountMenuItem(
                    icon = Icons.Outlined.Language,
                    label = "Langue",
                    sublabel = "Français",
                    onClick = { }
                )

                Spacer(modifier = Modifier.height(20.dp))

                AccountSectionTitle(title = "Aide & Support")
                AccountMenuItem(
                    icon = Icons.Outlined.HelpOutline,
                    label = "Centre d'aide",
                    sublabel = "FAQ et assistance technique",
                    onClick = { }
                )
                AccountMenuItem(
                    icon = Icons.Outlined.Info,
                    label = "À propos",
                    sublabel = "Version 1.0.0",
                    onClick = { }
                )

                Spacer(modifier = Modifier.height(40.dp))

                LogoutButton(onClick = { /* Handle logout logic */ })
            }
        }
    }
}

@Composable
private fun ProfileHeaderSection(
    userName: String = "Utilisateur HomeGaz",
    userEmail: String = "utilisateur@homegaz.cm",
    profileImageUrl: String? = null,
    onEditPhotoClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(120.dp)) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUrl != null) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .clickable { onEditPhotoClick() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = userName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(text = userEmail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(20.dp))
        OutlinedButton(onClick = onEditProfileClick, shape = RoundedCornerShape(12.dp)) {
            Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Modifier le profil")
        }
    }
}

@Composable
private fun AccountMenuItem(
    icon: ImageVector,
    label: String,
    sublabel: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(sublabel, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun AccountSectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
    ) {
        Icon(Icons.Outlined.Logout, null)
        Spacer(Modifier.width(8.dp))
        Text("Se déconnecter", fontWeight = FontWeight.Bold)
    }
}
