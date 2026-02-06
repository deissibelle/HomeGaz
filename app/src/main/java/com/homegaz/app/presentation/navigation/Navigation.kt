package com.homegaz.app.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.homegaz.app.presentation.auth.forgot.ForgotPasswordScreen
import com.homegaz.app.presentation.auth.login.LoginScreen
import com.homegaz.app.presentation.auth.register.RegisterScreen
import com.homegaz.app.presentation.home.HomeScreen
import com.homegaz.app.presentation.onboarding.OnboardingScreen

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Register : Screen("register")
    object Login : Screen("login")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Map : Screen("map")
    object Reservation : Screen("reservation/{gasPointId}") {
        fun createRoute(gasPointId: String) = "reservation/$gasPointId"
    }
    object Payment : Screen("payment/{reservationId}") {
        fun createRoute(reservationId: String) = "payment/$reservationId"
    }
    object Orders : Screen("orders")
    object OrderDetail : Screen("orders/{orderId}") {
        fun createRoute(orderId: String) = "orders/$orderId"
    }
    object Profile : Screen("profile")
}

@Composable
fun HomeGazNavigation(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Register
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Login
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Forgot Password
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                    }
                }
            )
        }

        // Home
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToMap = {
                    navController.navigate(Screen.Map.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToOrders = {
                    navController.navigate(Screen.Orders.route)
                }
            )
        }

        // Map (Phase 3 - To be implemented)
        composable(Screen.Map.route) {
            PlaceholderScreen(
                title = "Carte",
                message = "La carte interactive sera disponible dans la Phase 3",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Reservation (Phase 4 - To be implemented)
        composable(
            route = Screen.Reservation.route,
            arguments = listOf(navArgument("gasPointId") { type = NavType.StringType })
        ) {
            PlaceholderScreen(
                title = "Réservation",
                message = "Le système de réservation sera disponible dans la Phase 4",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Orders (Phase 6 - To be implemented)
        composable(Screen.Orders.route) {
            PlaceholderScreen(
                title = "Mes Commandes",
                message = "La liste des commandes sera disponible dans la Phase 6",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Profile
        composable(Screen.Profile.route) {
            PlaceholderScreen(
                title = "Profil",
                message = "La page de profil sera bientôt disponible",
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(
    title: String,
    message: String,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    Icons.Default.Construction,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
