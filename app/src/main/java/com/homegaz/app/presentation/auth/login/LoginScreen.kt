package com.homegaz.app.presentation.auth.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.homegaz.app.presentation.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onNavigateToHome()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Connexion") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Welcome Text
            Text(
                text = "Bienvenue sur HomeGaz",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Connectez-vous pour continuer",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { 
                        selectedTab = 0
                        viewModel.resetOtpSent()
                    },
                    text = { Text("E-mail") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { 
                        selectedTab = 1
                        viewModel.resetOtpSent()
                    },
                    text = { Text("Téléphone") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content
            when (selectedTab) {
                0 -> LoginEmailTab(
                    viewModel = viewModel,
                    uiState = uiState,
                    onNavigateToForgotPassword = onNavigateToForgotPassword
                )
                1 -> LoginPhoneTab(
                    viewModel = viewModel,
                    uiState = uiState
                )
            }

            // Error Message
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Register Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pas encore de compte ?")
                TextButton(onClick = onNavigateToRegister) {
                    Text("S'inscrire")
                }
            }
        }
    }
}

@Composable
fun LoginEmailTab(
    viewModel: AuthViewModel,
    uiState: com.homegaz.app.presentation.auth.AuthUiState,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail ou nom d'utilisateur") },
            leadingIcon = { 
                Icon(Icons.Default.Person, contentDescription = null) 
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.error != null && email.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            leadingIcon = { 
                Icon(Icons.Default.Lock, contentDescription = null) 
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Masquer" else "Afficher"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.error != null && password.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Forgot Password Link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onNavigateToForgotPassword) {
                Text("Mot de passe oublié ?")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = { 
                viewModel.clearError()
                viewModel.loginWithEmail(email, password) 
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotEmpty() && password.isNotEmpty() && !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (uiState.isLoading) "Connexion..." else "Se connecter")
        }
    }
}

@Composable
fun LoginPhoneTab(
    viewModel: AuthViewModel,
    uiState: com.homegaz.app.presentation.auth.AuthUiState
) {
    var countryCode by remember { mutableStateOf("+237") }
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Country Code + Phone Number
        Row(modifier = Modifier.fillMaxWidth()) {
            // Country Code Selector
            OutlinedTextField(
                value = countryCode,
                onValueChange = { countryCode = it },
                label = { Text("Code") },
                modifier = Modifier.width(100.dp),
                singleLine = true,
                enabled = !uiState.otpSent
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Phone Number
            OutlinedTextField(
                value = phone,
                onValueChange = { if (it.length <= 9) phone = it },
                label = { Text("Numéro") },
                leadingIcon = { 
                    Icon(Icons.Default.Phone, contentDescription = null) 
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.weight(1f),
                singleLine = true,
                enabled = !uiState.otpSent,
                supportingText = {
                    Text("Ex: 690123456")
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // OTP Field (shown after sending OTP)
        if (uiState.otpSent) {
            OutlinedTextField(
                value = otp,
                onValueChange = { if (it.length <= 6) otp = it },
                label = { Text("Code à 6 chiffres") },
                leadingIcon = { 
                    Icon(Icons.Default.Pin, contentDescription = null) 
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("Entrez le code reçu par SMS")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Resend OTP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { viewModel.sendPhoneOtp(phone, countryCode) },
                    enabled = !uiState.isLoading
                ) {
                    Text("Renvoyer le code")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Button
        if (!uiState.otpSent) {
            // Send OTP Button
            Button(
                onClick = { 
                    viewModel.clearError()
                    viewModel.sendPhoneOtp(phone, countryCode) 
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = phone.length == 9 && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (uiState.isLoading) "Envoi..." else "Envoyer le code")
            }
        } else {
            // Login Button
            Button(
                onClick = { 
                    viewModel.clearError()
                    viewModel.loginWithPhone(phone, countryCode, otp) 
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = otp.length == 6 && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (uiState.isLoading) "Connexion..." else "Se connecter")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Back to phone number
            OutlinedButton(
                onClick = { viewModel.resetOtpSent() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Modifier le numéro")
            }
        }
    }
}
