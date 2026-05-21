package cm.horion.homegaz.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cm.horion.homegaz.presentation.ui.components.common.BottomNavBar
import cm.horion.homegaz.presentation.ui.pages.account.AccountScreen
import cm.horion.homegaz.presentation.ui.pages.advices.AdvicesScreen
import cm.horion.homegaz.presentation.ui.pages.home.HomeScreen
import cm.horion.homegaz.presentation.ui.pages.reservations.ReservationsScreen
import cm.horion.homegaz.presentation.viewmodel.ReservationsViewModel

enum class Tab(val label: String) {
    HOME("Accueil"),
    RESERVATIONS("Réservations"),
    ADVICES("Conseils"),
    ACCOUNT("Compte");

    companion object {
        fun fromLabel(label: String) = entries.firstOrNull { it.label == label } ?: HOME
    }
}

@Composable
fun MainScreen(
    navController: NavController,
    reservationsViewModel : ReservationsViewModel,
    onRequestLocation     : () -> Unit,
    onRouteClick: (lat: Double, lng: Double) -> Unit = { _, _ -> },
    initialTab: String = Tab.HOME.label
) {
    var selectedTab by remember(initialTab) { mutableStateOf(Tab.fromLabel(initialTab)) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab   = selectedTab.label,
                onTabSelected = { label -> selectedTab = Tab.fromLabel(label) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                Tab.HOME -> HomeScreen(
                    navController = navController,
                    onRouteClick  = onRouteClick,
                    onRequestLocation = onRequestLocation
                )
                Tab.RESERVATIONS -> ReservationsScreen(navController = navController,viewModel     = reservationsViewModel )
                Tab.ADVICES      -> AdvicesScreen()
                Tab.ACCOUNT      -> AccountScreen(navController = navController)
            }
        }
    }
}