package cm.horion.homegaz.presentation.ui.pages.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.privacy_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Action rapide */ }) {
                        Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            // SECTION 1 : Utilisation des données
            PrivacySectionHeader(title = stringResource(R.string.privacy_sec_how_we_use))
            PrivacyListItem(label = stringResource(R.string.privacy_item_permissions))
            PrivacyListItem(label = stringResource(R.string.privacy_item_download))
            PrivacyListItem(label = stringResource(R.string.privacy_item_clear_search))
            PrivacyListItem(label = stringResource(R.string.privacy_item_demographics))
            PrivacyListItem(
                label = stringResource(R.string.privacy_item_research),
            )
            PrivacyListItem(
                label = stringResource(R.string.privacy_item_ai_opt_in),
            )

            // Espaceur gris
            Spacer(modifier = Modifier.height(16.dp).fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)))

            // SECTION 2 : Qui peut vous contacter
            PrivacySectionHeader(title = stringResource(R.string.privacy_sec_contacts))
            PrivacyListItem(label = stringResource(R.string.privacy_item_invitations_connect))
            PrivacyListItem(label = stringResource(R.string.privacy_item_invitations_network))
            PrivacyListItem(label = stringResource(R.string.privacy_item_messages))
            PrivacyListItem(
                label = stringResource(R.string.privacy_item_research_studies),
            )
            PrivacyListItem(label = stringResource(R.string.privacy_item_marketing_emails))

            // Espaceur gris
            Spacer(modifier = Modifier.height(16.dp).fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)))

            // SECTION 3 : Messagerie
            PrivacySectionHeader(title = stringResource(R.string.privacy_sec_messaging))
            PrivacyListItem(label = stringResource(R.string.privacy_item_read_receipts))
        }
    }
}

@Composable
private fun PrivacySectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        ),
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    )
}

@Composable
private fun PrivacyListItem(
    label: String,
    status: String? = null,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.weight(1f)
            )

        }
        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}