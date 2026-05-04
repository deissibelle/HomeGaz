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
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.TipsAndUpdates
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
    val selectedIndex =
        navItems.indexOfFirst { it.id == selectedTab }.coerceAtLeast(0)

    val animatedXOffset by animateDpAsState(
        targetValue = (itemWidth * selectedIndex) + (itemWidth / 2),
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "BumpAnimation"
    )

    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.homeGazColors.surface)
            .navigationBarsPadding()
    ) {

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
                .height(75.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->

                val isSelected = selectedTab == item.id

                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        primaryColor
                    } else {
                        primaryColor.copy(alpha = 5f)
                    },
                    animationSpec = tween(300),
                    label = "IconColorAnimation"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null,
                            onClick = {
                                onTabSelected(item.id)
                            }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Icon(
                        imageVector = if (isSelected) {
                            item.iconFilled
                        } else {
                            item.iconOutlined
                        },
                        contentDescription = item.label,
                        modifier = Modifier.size(26.dp),
                        tint = contentColor
                    )

                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor
                    )
                }
            }
        }
    }
}