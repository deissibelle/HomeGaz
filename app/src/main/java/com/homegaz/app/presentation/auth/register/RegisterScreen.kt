package com.homegaz.app.presentation.auth.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.homegaz.app.domain.repository.PasswordStrength
import com.homegaz.app.presentation.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
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
                title = { Text("Inscription") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(Icons.Default.ArrowBack, "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("E-mail") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Téléphone") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content
            when (selectedTab) {
                0 -> RegisterEmailTab(viewModel, uiState)
                1 -> RegisterPhoneTab(viewModel, uiState)
            }

            // Error Message
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Login Link
            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Vous avez déjà un compte ? Se connecter")
            }
        }
    }
}

@Composable
fun RegisterEmailTab(
    viewModel: AuthViewModel,
    uiState: com.homegaz.app.presentation.auth.AuthUiState
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var acceptTerms by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Adresse e-mail") },
            leadingIcon = { Icon(Icons.Default.Email, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                viewModel.updatePasswordStrength(it)
            },
            label = { Text("Mot de passe") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        null
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Password Strength Indicator
        if (password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            PasswordStrengthIndicator(uiState.passwordStrength)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // OTP Field
        OutlinedTextField(
            value = otp,
            onValueChange = { if (it.length <= 6) otp = it },
            label = { Text("Code à 6 chiffres") },
            leadingIcon = { Icon(Icons.Default.Pin, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.otpSent,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Send OTP Button
        Button(
            onClick = { viewModel.registerWithEmail(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotEmpty() && password.length >= 8 && !uiState.otpSent && !uiState.isLoading
        ) {
            if (uiState.isLoading && !uiState.otpSent) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Envoyer le code")
            }
        }

        // Verify OTP Button
        if (uiState.otpSent) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.verifyEmailOtp(email, otp, password) },
                modifier = Modifier.fillMaxWidth(),
                enabled = otp.length == 6 && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Suivant")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Terms Checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = acceptTerms,
                onCheckedChange = { acceptTerms = it }
            )
            Text(
                text = "Recevoir du contenu tendance, des offres et des promotions",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun RegisterPhoneTab(
    viewModel: AuthViewModel,
    uiState: com.homegaz.app.presentation.auth.AuthUiState
) {
    var countryCode by remember { mutableStateOf("+237") }
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Country Code + Phone
        Row(modifier = Modifier.fillMaxWidth()) {
            // Country Code
            OutlinedTextField(
                value = countryCode,
                onValueChange = { countryCode = it },
                label = { Text("Code") },
                modifier = Modifier.width(100.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Phone Number
            OutlinedTextField(
                value = phone,
                onValueChange = { if (it.length <= 9) phone = it },
                label = { Text("Numéro de téléphone") },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // OTP Field
        OutlinedTextField(
            value = otp,
            onValueChange = { if (it.length <= 6) otp = it },
            label = { Text("Code à 6 chiffres") },
            leadingIcon = { Icon(Icons.Default.Pin, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.otpSent,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Send OTP Button
        Button(
            onClick = { viewModel.registerWithPhone(phone, countryCode) },
            modifier = Modifier.fillMaxWidth(),
            enabled = phone.length == 9 && !uiState.otpSent && !uiState.isLoading
        ) {
            if (uiState.isLoading && !uiState.otpSent) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Envoyer le code")
            }
        }

        // Verify OTP Button
        if (uiState.otpSent) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.verifyPhoneOtp(phone, countryCode, otp) },
                modifier = Modifier.fillMaxWidth(),
                enabled = otp.length == 6 && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Suivant")
                }
            }
        }
    }
}

@Composable
fun PasswordStrengthIndicator(strength: PasswordStrength) {
    val (color, text) = when (strength) {
        PasswordStrength.WEAK -> MaterialTheme.colorScheme.error to "Faible"
        PasswordStrength.MEDIUM -> Color(0xFFFFA726) to "Moyen"
        PasswordStrength.STRONG -> MaterialTheme.colorScheme.primary to "Fort"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        LinearProgressIndicator(
            progress = when (strength) {
                PasswordStrength.WEAK -> 0.33f
                PasswordStrength.MEDIUM -> 0.66f
                PasswordStrength.STRONG -> 1f
            },
            modifier = Modifier.weight(1f),
            color = color
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}
