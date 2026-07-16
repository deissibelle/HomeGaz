package cm.horion.homegaz.presentation.ui.components.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme // Pour détecter le thème
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.domain.model.common.NavItem
import cm.horion.homegaz.presentation.ui.theme.homeGazColors

@Composable
fun BottomNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val navItems = listOf(
        NavItem(
            id = "Accueil",
            iconOutlined = Icons.Outlined.Home,
            iconFilled = Icons.Filled.Home,
            label = "Accueil"
        ),
        NavItem(
            id = "Réservations",
            iconOutlined = Icons.Outlined.ShoppingCart,
            iconFilled = Icons.Filled.ShoppingCart,
            label = "Réservations"
        ),
        NavItem(
            id = "Conseils",
            iconOutlined = Icons.Outlined.TipsAndUpdates,
            iconFilled = Icons.Filled.TipsAndUpdates,
            label = "Conseils"
        ),
        NavItem(
            id = "Compte",
            iconOutlined = Icons.Outlined.AccountCircle,
            iconFilled = Icons.Filled.AccountCircle,
            label = "Compte"
        )
    )

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val itemWidth = screenWidth / navItems.size
    val selectedIndex = navItems.indexOfFirst { it.id == selectedTab }.coerceAtLeast(0)

    val animatedXOffset by animateDpAsState(
        targetValue = (itemWidth * selectedIndex) + (itemWidth / 2),
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "BumpAnimation"
    )

    // 🎯 Couleurs issues directement du MaterialTheme (s'adaptent dynamiquement)
    val primaryColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant // Gris neutre et doux
    val dividerColor = MaterialTheme.colorScheme.outlineVariant // Bordure subtile de séparation

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface) // Gris carbone en Dark, Blanc en Light
            .navigationBarsPadding()
    ) {

        // 🎯 Ajout d'une fine bordure au-dessus pour décoller la barre en mode sombre
        HorizontalDivider(
            color = dividerColor,
            thickness = 0.5.dp,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Indicateur animé au-dessus de l'icône sélectionnée
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(15.dp)
        ) {
            val bumpWidth = 55.dp.toPx()
            val bumpHeight = 8.dp.toPx()
            val indicatorWidth = 45.dp.toPx()
            val x = animatedXOffset.toPx()
            val y = 10.dp.toPx()

            val leftPath = Path().apply {
                moveTo(x - bumpWidth / 1.2f, y)
                cubicTo(
                    x - bumpWidth / 2,
                    y,
                    x - bumpWidth / 3,
                    y - bumpHeight,
                    x - indicatorWidth / 2,
                    y - bumpHeight
                )
            }

            drawPath(
                path = leftPath,
                color = Color.Transparent,
                style = Stroke(width = 2.dp.toPx())
            )

            // La petite ligne indicatrice active
            drawLine(
                color = primaryColor,
                start = androidx.compose.ui.geometry.Offset(
                    x - indicatorWidth / 2,
                    y - bumpHeight
                ),
                end = androidx.compose.ui.geometry.Offset(
                    x + indicatorWidth / 2,
                    y - bumpHeight
                ),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            val rightPath = Path().apply {
                moveTo(x + indicatorWidth / 2, y - bumpHeight)
                cubicTo(
                    x + bumpWidth / 3,
                    y - bumpHeight,
                    x + bumpWidth / 2,
                    y,
                    x + bumpWidth / 1.2f,
                    y
                )
            }

            drawPath(
                path = rightPath,
                color = Color.Transparent,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp), // Hauteur légèrement augmentée pour un confort de clic accru
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = selectedTab == item.id

                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) primaryColor else unselectedColor,
                    animationSpec = tween(250),
                    label = "IconColorAnimation"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null, // Pas de ripple rectangle moche
                            onClick = { onTabSelected(item.id) }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isSelected) item.iconFilled else item.iconOutlined,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp),
                        tint = contentColor
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor,
                        // Uniquement gras quand c'est sélectionné pour donner du poids visuel
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}