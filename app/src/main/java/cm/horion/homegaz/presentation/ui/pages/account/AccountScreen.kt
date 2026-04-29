package cm.horion.homegaz.presentation.ui.pages.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.auth.AuthContext
import cm.horion.homegaz.domain.model.common.Screen
import cm.horion.homegaz.presentation.ui.components.account.AccountMenuItem
import cm.horion.homegaz.presentation.ui.components.account.ProfileHeaderSection
import cm.horion.homegaz.presentation.ui.components.account.SectionContainer
import cm.horion.homegaz.presentation.ui.pages.auth.AuthGuardScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavController) {
    val isLoggedIn by remember { mutableStateOf(false) }

    if (isLoggedIn) {
        AuthGuardScreen(
            authContext = AuthContext(
                title = stringResource(R.string.auth_title_account),
                description = stringResource(R.string.auth_desc_account),
                icon = Icons.Outlined.Person
            ),
            onLoginClick = { },
            onRegisterClick = { },
            onForgotPasswordClick = { }
        )
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    tonalElevation = 0.dp
                ) {
                    CenterAlignedTopAppBar(
                        windowInsets = WindowInsets(0, 0, 0, 0),
                        title = {
                            Text(
                                text = stringResource(R.string.account_title),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            )
                        },

                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .verticalScroll(rememberScrollState())
            ) {
                ProfileHeaderSection()

                Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 32.dp)) {

                    SectionContainer(title = stringResource(R.string.account_section_consumption)) {
                        AccountMenuItem(
                            icon = Icons.Outlined.LocalFireDepartment,
                            label = stringResource(R.string.account_menu_gaz_profile),
                            sublabel = stringResource(R.string.account_sub_gaz_profile),
                            onClick = { navController.navigate(Screen.GazProfile.route) }
                        )
                    }

                    SectionContainer(title = stringResource(R.string.account_section_settings)) {
                        AccountMenuItem(
                            icon = Icons.Outlined.Notifications,
                            label = stringResource(R.string.account_menu_notifications),
                            sublabel = stringResource(R.string.account_sub_notifications),
                            onClick = { }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 56.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        AccountMenuItem(
                            icon = Icons.Outlined.SettingsSuggest,
                            label = stringResource(R.string.account_menu_display),
                            sublabel = stringResource(R.string.account_sub_display),
                            onClick = { }
                        )
                    }

                    SectionContainer(title = stringResource(R.string.account_section_support)) {
                        AccountMenuItem(
                            icon = Icons.Outlined.HelpOutline,
                            label = stringResource(R.string.account_menu_help),
                            sublabel = stringResource(R.string.account_sub_help),
                            onClick = { }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 56.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        AccountMenuItem(
                            icon = Icons.Outlined.VerifiedUser,
                            label = stringResource(R.string.account_menu_privacy),
                            sublabel = stringResource(R.string.account_sub_privacy),
                            onClick = { }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.account_menu_logout), fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = stringResource(R.string.account_version_format, "1.0.0", 24),
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
