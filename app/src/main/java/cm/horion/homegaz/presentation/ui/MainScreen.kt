package cm.horion.homegaz.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.auth.AuthContext
import cm.horion.homegaz.presentation.ui.components.common.BottomNavBar
import cm.horion.homegaz.presentation.ui.pages.account.AccountScreen
import cm.horion.homegaz.presentation.ui.pages.auth.AuthGuardScreen
import cm.horion.homegaz.presentation.ui.pages.home.HomeScreen
import cm.horion.homegaz.presentation.ui.pages.reservations.ReservationsScreen
import cm.horion.homegaz.presentation.viewmodel.ConsumerViewModel
import cm.horion.homegaz.presentation.viewmodel.ReservationsViewModel

enum class Tab(val label: String) {
    HOME("Accueil"),
    RESERVATIONS("Réservations"),
    ACCOUNT("Compte");

    companion object {
        fun fromLabel(label: String) = entries.firstOrNull { it.label == label } ?: HOME
    }
}

private val TabSaver = Saver<Tab, String>(
    save    = { it.name },
    restore = { Tab.valueOf(it) }
)

@Composable
fun MainScreen(
    backStack              : NavBackStack<NavKey>,
    reservationsViewModel  : ReservationsViewModel,
    consumerViewModel      : ConsumerViewModel,
    onRequestLocation      : () -> Unit,
    onRouteClick           : (lat: Double, lng: Double) -> Unit = { _, _ -> },
    requestedTab            : Tab? = null,
    onRequestedTabConsumed  : () -> Unit = {},

    isLoggedIn              : Boolean,
    onSsoLoginCall          : () -> Unit,
    onSsoLogoutCall         : () -> Unit
) {
    var selectedTab by rememberSaveable(stateSaver = TabSaver) { mutableStateOf(Tab.HOME) }


    LaunchedEffect(requestedTab) {
        requestedTab?.let {
            selectedTab = it
            onRequestedTabConsumed()
        }
    }
    BackHandler(enabled = selectedTab != Tab.HOME) {
        selectedTab = Tab.HOME
    }

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
                    backStack         = backStack,
                    onRouteClick      = onRouteClick,
                    onRequestLocation = onRequestLocation
                )
            }

            if (selectedTab == Tab.RESERVATIONS) {
                if (!isLoggedIn) {
                    AuthGuardScreen(
                        authContext = AuthContext(
                            title       = stringResource(R.string.auth_title_reservations),
                            description = stringResource(R.string.auth_desc_reservations),
                            icon        = Icons.Default.ReceiptLong,
                        ),
                        onLoginClick          = onSsoLoginCall,
                        onRegisterClick       = onSsoLoginCall,
                        onForgotPasswordClick = {},
                    )
                } else {
                    ReservationsScreen(
                        viewModel            = reservationsViewModel,
                        onNavigateToHomeTab  = { selectedTab = Tab.HOME }
                    )
                }
            }

            if (selectedTab == Tab.ACCOUNT) {
                if (!isLoggedIn) {
                    AuthGuardScreen(
                        authContext = AuthContext(
                            title       = stringResource(R.string.auth_title_account),
                            description = stringResource(R.string.auth_desc_account),
                            icon        = Icons.Default.AccountCircle,
                        ),
                        onLoginClick          = onSsoLoginCall,
                        onRegisterClick       = onSsoLoginCall,
                        onForgotPasswordClick = {},
                    )
                } else {
                    AccountScreen(
                        backStack       = backStack,
                        onSsoLogoutCall = onSsoLogoutCall
                    )
                }
            }
        }
    }
}