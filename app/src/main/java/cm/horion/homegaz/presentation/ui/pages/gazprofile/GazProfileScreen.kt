package cm.horion.homegaz.presentation.ui.pages.gazprofile

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cm.horion.homegaz.presentation.state.GazProfileUiState
import cm.horion.homegaz.presentation.ui.components.gazprofile.GazProfileFormCard
import cm.horion.homegaz.presentation.ui.components.gazprofile.GazProfileHeader
import cm.horion.homegaz.presentation.ui.components.gazprofile.GazProfileSaveButton
import cm.horion.homegaz.presentation.viewmodel.GazProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GazProfileScreen(
    onBackClick : () -> Unit = {},
    onSaved     : () -> Unit = {},
    viewModel   : GazProfileViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.detectLocationFromGps()
    }

    val onDetectGps: () -> Unit = {
        when {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.detectLocationFromGps()
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onSaved()
    }

    GazProfileContent(
        uiState          = uiState,
        onBackClick      = onBackClick,
        onCapacityChange = viewModel::onCapacityChange,
        onBrandChange    = viewModel::onBrandChange,
        onLocationChange = viewModel::onLocationChange,
        onRegionChange   = viewModel::onRegionChange,
        onVilleChange    = viewModel::onVilleChange,
        onQuartierChange = viewModel::onQuartierChange,
        onLieuDitChange  = viewModel::onLieuDitChange,
        onDetectGps      = onDetectGps,
        onSave           = { viewModel.saveProfile(onSaved) }
    )
}

@Composable
private fun GazProfileContent(
    uiState          : GazProfileUiState,
    onBackClick      : () -> Unit,
    onCapacityChange : (String) -> Unit,
    onBrandChange    : (String) -> Unit,
    onLocationChange : (String) -> Unit,
    onRegionChange   : (String) -> Unit,
    onVilleChange    : (String) -> Unit,
    onQuartierChange : (String) -> Unit,
    onLieuDitChange  : (String) -> Unit,
    onDetectGps      : () -> Unit,
    onSave           : () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                GazProfileHeader(onBackClick = onBackClick)

                Spacer(modifier = Modifier.height(20.dp))

                GazProfileFormCard(
                    uiState          = uiState,
                    onCapacityChange = onCapacityChange,
                    onBrandChange    = onBrandChange,
                    onLocationChange = onLocationChange,
                    onRegionChange   = onRegionChange,
                    onVilleChange    = onVilleChange,
                    onQuartierChange = onQuartierChange,
                    onLieuDitChange  = onLieuDitChange,
                    onDetectGps      = onDetectGps,
                )

                uiState.errorMessage?.let { msg ->
                    Text(
                        text     = msg,
                        color    = MaterialTheme.colorScheme.error,
                        style    = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            GazProfileSaveButton(
                isSaving  = uiState.isSaving,
                isEnabled = uiState.isFormValid && !uiState.isSaving,
                onSave    = onSave
            )
        }
    }
}