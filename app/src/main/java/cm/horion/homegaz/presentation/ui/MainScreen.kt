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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import cm.horion.homegaz.presentation.ui.pages.home.HomeScreen

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
    navController         : NavController,
    reservationsViewModel : ReservationsViewModel,
    onRequestLocation     : () -> Unit,
    onRouteClick          : (lat: Double, lng: Double) -> Unit = { _, _ -> },
    initialTab            : String = Tab.HOME.label
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
            // ─────────────────────────────────────────────────────────────────
            // HOME : toujours dans la composition, jamais détruit.
            // graphicsLayer(alpha=0) le rend invisible sans le retirer du tree.
            // pointerInput bloque les interactions quand l'onglet est inactif.
            // La MapView et ses objets JNI survivent aux changements d'onglet.
            // ─────────────────────────────────────────────────────────────────
            val homeActive = selectedTab == Tab.HOME
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = if (homeActive) 1f else 0f }
                    .then(
                        if (!homeActive)
                            Modifier.pointerInput(Unit) {
                                detectTapGestures { /* absorbe tous les taps */ }
                            }
                        else Modifier
                    )
            ) {
                HomeScreen(
                    navController     = navController,
                    onRouteClick      = onRouteClick,
                    onRequestLocation = onRequestLocation
                )
            }

            // Les autres onglets peuvent être créés/détruits librement
            // car ils ne contiennent pas d'objets JNI natifs à préserver.
            if (selectedTab == Tab.RESERVATIONS) {
                ReservationsScreen(
                    navController = navController,
                    viewModel     = reservationsViewModel
                )
            }

            if (selectedTab == Tab.ADVICES) {
                AdvicesScreen()
            }

            if (selectedTab == Tab.ACCOUNT) {
                AccountScreen(navController = navController)
            }
        }
    }
}