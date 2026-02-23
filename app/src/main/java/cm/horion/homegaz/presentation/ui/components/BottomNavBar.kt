package cm.horion.homegaz.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.utils.ThemeColor

@Composable
fun BottomNavBar() {
    // Accueil est sélectionné par défaut
    var selectedTab by remember { mutableStateOf("Accueil") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .background(ThemeColor.Primary),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val navItems = listOf(
            Triple("Accueil", R.drawable.home, "Accueil"),
            Triple("Réservations", R.drawable.shopping_cart, "Réservations"),
            Triple("Conseils", R.drawable.light_bulb, "Astuces"),
            Triple("Compte", R.drawable.account, "Compte")
        )

        navItems.forEach { (label, icon, id) ->
            val isSelected = selectedTab == id
            val contentColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false, radius = 40.dp, color = Color.White),
                        onClick = { selectedTab = id }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = label,
                    modifier = Modifier.size(26.dp),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = label, fontSize = 11.sp, color = contentColor)
            }
        }
    }
}