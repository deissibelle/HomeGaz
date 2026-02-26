package cm.horion.homegaz.presentation.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.utils.ThemeColor

data class NavItem(
    val label: String,
    val iconOutlined: Int,   // ex: R.drawable.home
    val iconFilled: Int,     // ex: R.drawable.home_filled
    val id: String
)

@Composable
fun BottomNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val navItems = listOf(
        NavItem("Accueil",      R.drawable.home_outlined,          R.drawable.home_filled,          "Accueil"),
        NavItem("Réservations", R.drawable.shopping_cart, R.drawable.shopping_cart, "Réservations"),
        NavItem("Conseils",     R.drawable.light_bulb_outlined,    R.drawable.light_bulb_filled,    "Conseils"),
        NavItem("Compte",       R.drawable.account,       R.drawable.account,       "Compte")
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .navigationBarsPadding()
            .background(ThemeColor.Primary),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        navItems.forEach { item ->
            val isSelected = selectedTab == item.id
            val contentColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
            val iconRes = if (isSelected) item.iconFilled else item.iconOutlined

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false, radius = 40.dp, color = Color.White),
                        onClick = { onTabSelected(item.id) }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = item.label,
                    modifier = Modifier.size(width = 34.dp, height = 34.dp),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.label, fontSize = 14.sp, color = contentColor)
            }
        }
    }
}