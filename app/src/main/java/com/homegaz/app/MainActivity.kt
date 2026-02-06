package com.homegaz.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.homegaz.app.data.local.PreferencesManager
import com.homegaz.app.presentation.navigation.HomeGazNavigation
import com.homegaz.app.presentation.navigation.Screen
import com.homegaz.app.presentation.theme.HomeGazTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HomeGazTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var startDestination by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(Unit) {
                        val onboardingCompleted =
                            preferencesManager.isOnboardingCompleted().first()

                        val authTokens = preferencesManager.getAuthTokens()

                        startDestination = when {
                            !onboardingCompleted -> Screen.Onboarding.route
                            authTokens != null -> Screen.Home.route
                            else -> Screen.Login.route
                        }
                    }

                    when (startDestination) {
                        null -> {

                            LoadingScreen()
                        }
                        else -> {
                            HomeGazNavigation(
                                navController = navController,
                                startDestination = startDestination!!
                            )
                        }
                    }
                }
            }


    }



    }
}


@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
