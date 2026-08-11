package cm.horion.homegaz.presentation.ui.components.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.common.NavItem



@Composable
fun BottomNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val navItems = remember {
        listOf(
            NavItem(
                Id = "HOME",
                labelResId = R.string.nav_home,
                iconOutlined = Icons.Outlined.Home,
                iconFilled = Icons.Filled.Home
            ),
            NavItem(
                Id = "RESERVATIONS",
                labelResId = R.string.nav_reservations,
                iconOutlined = Icons.Outlined.ShoppingCart,
                iconFilled = Icons.Filled.ShoppingCart
            ),
            NavItem(
                Id = "ACCOUNT",
                labelResId = R.string.nav_account,
                iconOutlined = Icons.Outlined.AccountCircle,
                iconFilled = Icons.Filled.AccountCircle
            )
        )
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val itemWidth = screenWidth / navItems.size

    // Comparaison basée sur l'ID technique invariable
    val selectedIndex = navItems.indexOfFirst { it.Id == selectedTab }.coerceAtLeast(0)

    val animatedXOffset by animateDpAsState(
        targetValue = (itemWidth * selectedIndex) + (itemWidth / 2),
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "BumpAnimation"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
    ) {

        HorizontalDivider(
            color = dividerColor,
            thickness = 0.5.dp,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Indicateur Canvas animé au-dessus de l'icône sélectionnée
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
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                // Comparaison basée sur l'ID technique invariable
                val isSelected = selectedTab == item.Id

                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) primaryColor else unselectedColor,
                    animationSpec = tween(250),
                    label = "IconColorAnimation"
                )

                // Résolution dynamique de la langue à chaque recomposition
                val translatedLabel = stringResource(id = item.labelResId)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelected(item.Id) } // Renvoie "HOME", "ACCOUNT", etc.
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isSelected) item.iconFilled else item.iconOutlined,
                        contentDescription = translatedLabel,
                        modifier = Modifier.size(24.dp),
                        tint = contentColor
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = translatedLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
