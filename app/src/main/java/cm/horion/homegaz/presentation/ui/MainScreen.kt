package cm.horion.homegaz.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.auth.AuthContext
import cm.horion.homegaz.presentation.ui.pages.auth.AuthGuardScreen
import cm.horion.homegaz.presentation.ui.pages.home.HomeScreen
import cm.horion.homegaz.presentation.viewmodel.ConsumerViewModel

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
    consumerViewModel     : ConsumerViewModel,
    onRequestLocation     : () -> Unit,
    onRouteClick          : (lat: Double, lng: Double) -> Unit = { _, _ -> },
    initialTab            : String = Tab.HOME.label,

    isLoggedIn            : Boolean,
    onSsoLoginCall        : () -> Unit,
    onSsoLogoutCall       : () -> Unit
) {
    var selectedTab by remember(initialTab) { mutableStateOf(Tab.fromLabel(initialTab)) }
    var currentTab by remember { mutableStateOf(Tab.fromLabel(initialTab)) }


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

//            val consumerState by consumerViewModel.uiState.collectAsState()
//            AnimatedVisibility(
//                visible  = consumerState.isRefiningLocation,
//                enter    = fadeIn(),
//                exit     = fadeOut(),
//                modifier = Modifier.align(Alignment.TopCenter)
//            ) {
//                LinearProgressIndicator(
//                    modifier   = Modifier
//                        .fillMaxWidth()
//                        .height(3.dp),
//                    color      = MaterialTheme.colorScheme.primary,
//                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
//                )
//            }

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
                    consumerViewModel = consumerViewModel,
                    navController     = navController,
                    onRouteClick      = onRouteClick,
                    onRequestLocation = onRequestLocation
                )
            }

            // Les autres onglets peuvent être créés/détruits librement
            // car ils ne contiennent pas d'objets JNI natifs à préserver.

            if (selectedTab == Tab.RESERVATIONS) {
                if (!isLoggedIn) {
                    AuthGuardScreen(
                        authContext = AuthContext(
                            title       = stringResource(cm.horion.homegaz.R.string.auth_title_reservations),
                            description = stringResource(R.string.auth_desc_reservations),
                            icon        = Icons.Default.ReceiptLong,
                        ),
                        onLoginClick          = onSsoLoginCall,
                        onRegisterClick       = onSsoLoginCall,
                        onForgotPasswordClick = {},
                    )

                } else {
                    ReservationsScreen(
                        navController = navController,
                        viewModel     = reservationsViewModel,
                        onNavigateToHomeTab = { selectedTab = Tab.HOME }

                    )
                }

            }

            if (selectedTab == Tab.ADVICES) {
                AdvicesScreen()
            }

            if (selectedTab == Tab.ACCOUNT) {
                if (!isLoggedIn) {
                    AuthGuardScreen(
                        authContext = AuthContext(
                            title       = stringResource(cm.horion.homegaz.R.string.auth_title_reservations),
                            description = stringResource(R.string.auth_desc_reservations),
                            icon        = Icons.Default.ReceiptLong,
                        ),
                        onLoginClick          = onSsoLoginCall,
                        onRegisterClick       = onSsoLoginCall,
                        onForgotPasswordClick = {},
                    )

                } else {
                    AccountScreen(
                        navController = navController,
                        onSsoLogoutCall = onSsoLogoutCall
                    )

                }
            }
        }
    }
}