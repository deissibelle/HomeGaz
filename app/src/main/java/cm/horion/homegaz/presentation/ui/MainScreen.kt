package cm.horion.homegaz.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cm.horion.homegaz.presentation.ui.components.common.BottomNavBar
import cm.horion.homegaz.presentation.ui.home.HomeScreen
import cm.horion.homegaz.presentation.ui.advices.AdvicesScreen

@Composable
fun MainScreen(
    onMarkerClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Accueil") }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                "Accueil"      -> HomeScreen(onMarkerClick = onMarkerClick)
                "Conseils"     -> AdvicesScreen()
                // "Réservations" -> ReservationsScreen()
                // "Compte"       -> AccountScreen()
            }
        }
    }
}