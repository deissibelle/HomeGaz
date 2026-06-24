package cm.horion.homegaz.presentation.ui.pages.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.auth.AuthContext
import cm.horion.homegaz.presentation.ui.theme.LocalThemeIsDark
import cm.horion.homegaz.presentation.ui.theme.PrimaryContainerDark
import cm.horion.homegaz.presentation.ui.theme.PrimaryContainerLight
import cm.horion.homegaz.presentation.ui.theme.SecondaryContainerDark
import cm.horion.homegaz.presentation.ui.theme.SecondaryContainerLight

@Composable
fun AuthGuardScreen(
    authContext: AuthContext,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val isDark = LocalThemeIsDark.current

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val iconAlpha    by animateFloatAsState(if (visible) 1f else 0f, tween(450, 0),    label = "a_icon")
    val iconOffsetY  by animateFloatAsState(if (visible) 0f else 24f, tween(450, 0),   label = "o_icon")
    val titleAlpha   by animateFloatAsState(if (visible) 1f else 0f, tween(450, 110),  label = "a_title")
    val titleOffsetY by animateFloatAsState(if (visible) 0f else 24f, tween(450, 110), label = "o_title")
    val btnAlpha     by animateFloatAsState(if (visible) 1f else 0f, tween(450, 220),  label = "a_btn")
    val btnOffsetY   by animateFloatAsState(if (visible) 0f else 24f, tween(450, 220), label = "o_btn")


    val infiniteTransition = rememberInfiniteTransition(label = "iconFloat")
    val floatY by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = -7f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    val haloOuter = if (isDark) PrimaryContainerDark else PrimaryContainerLight
    val haloInner = if (isDark) SecondaryContainerDark else SecondaryContainerLight

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-70).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            haloOuter.copy(alpha = 0.50f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0f)
                        )
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = (-60).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            haloInner.copy(alpha = 0.35f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0f)
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier.graphicsLayer(
                    alpha        = iconAlpha,
                    translationY = iconOffsetY + floatY
                ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.22f),
                            shape = CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.40f),
                            shape = CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = authContext.icon,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.onPrimary,
                        modifier           = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            //  Titre
            Text(
                text      = authContext.title,
                style     = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color     = MaterialTheme.colorScheme.primary,
                modifier  = Modifier.graphicsLayer(
                    alpha        = titleAlpha,
                    translationY = titleOffsetY
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            Text(
                text       = authContext.description,
                style      = MaterialTheme.typography.bodyLarge,
                fontSize   = 14.sp,
                lineHeight = 22.sp,
                textAlign  = TextAlign.Center,
                color      = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier   = Modifier
                    .graphicsLayer(alpha = titleAlpha, translationY = titleOffsetY)
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Bouton principal
            Button(
                onClick   = onLoginClick,
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .graphicsLayer(alpha = btnAlpha, translationY = btnOffsetY),
                shape     = RoundedCornerShape(16.dp),
                colors    = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor   = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 3.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Text(
                    text       = stringResource(R.string.auth_login_btn),
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bouton secondaire
            OutlinedButton(
                onClick  = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .graphicsLayer(alpha = btnAlpha, translationY = btnOffsetY),
                shape    = RoundedCornerShape(16.dp),
                border   = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)),
                colors   = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text       = stringResource(R.string.auth_register_btn),
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            //  Séparateur
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.graphicsLayer(alpha = btnAlpha)
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color    = MaterialTheme.colorScheme.outlineVariant
                )
                Text(
                    text  = "  ${stringResource(R.string.auth_or_separator)}  ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color    = MaterialTheme.colorScheme.outlineVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Mot de passe oublié
            TextButton(
                onClick  = onForgotPasswordClick,
                modifier = Modifier.graphicsLayer(alpha = btnAlpha)
            ) {
                Text(
                    text       = stringResource(R.string.auth_forgot_password),
                    color      = MaterialTheme.colorScheme.secondary,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            //  Pied de page légal
            Text(
                text       = stringResource(R.string.auth_legal_footer),
                style      = MaterialTheme.typography.labelSmall,
                fontSize   = 11.sp,
                textAlign  = TextAlign.Center,
                color      = MaterialTheme.colorScheme.outline.copy(alpha = 0.65f),
                lineHeight = 16.sp,
                modifier   = Modifier
                    .graphicsLayer(alpha = btnAlpha)
                    .padding(horizontal = 4.dp)
            )
        }
    }
}