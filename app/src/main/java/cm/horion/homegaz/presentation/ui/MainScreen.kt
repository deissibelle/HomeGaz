package cm.horion.homegaz.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cm.horion.homegaz.presentation.ui.components.common.BottomNavBar
import cm.horion.homegaz.presentation.ui.pages.advices.AdvicesScreen
import cm.horion.homegaz.presentation.ui.pages.home.HomeScreen

@Composable
fun MainScreen(
    navController   : NavController,
    onMarkerClick   : (pointId: String) -> Unit = {},
    onRefreshClick  : () -> Unit = {},
    onBuyClick      : (pointId: String) -> Unit = {},
    pendingPointId  : String? = null,
    userLat         : Double? = null,
    userLng         : Double? = null,
    locationGranted : Boolean = false,
    locationDenied  : Boolean = false
) {
    var selectedTab by remember { mutableStateOf(Tab.HOME) }

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
                    onMarkerClick   = onMarkerClick,
                    onRefreshClick  = onRefreshClick,
                    onBuyClick      = onBuyClick,
                    pendingPointId  = pendingPointId,
                    userLat         = userLat,
                    userLng         = userLng,
                    locationGranted = locationGranted,
                    locationDenied  = locationDenied,
                    navController   = navController
                )
                Tab.ADVICES -> AdvicesScreen()
            }
        }
    }
}

private enum class Tab(val label: String) {
    HOME("Accueil"),
    ADVICES("Conseils");

    companion object {
        fun fromLabel(label: String) = entries.firstOrNull { it.label == label } ?: HOME
    }
}