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
import cm.horion.homegaz.domain.model.consommateur.dto.Company
import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle
import cm.horion.homegaz.domain.model.consommateur.dto.GazSize
import cm.horion.homegaz.domain.model.consommateur.dto.GazType
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

    // ── 1. Marques disponibles (BUTANE) ──
    val distributorOptions = remember(uiState.availableBottles) {
        val butaneBottles = uiState.availableBottles.filter { it.gazType == GazType.BUTANE }
        if (butaneBottles.isEmpty()) {
            Company.entries.map { it.name }
        } else {
            butaneBottles.map { it.company.name }.distinct()
        }
    }

    // ── 2. Tailles disponibles selon la marque choisie ──
    val weightOptions = remember(uiState.availableBottles, uiState.brand) {
        var butaneBottles = uiState.availableBottles.filter { it.gazType == GazType.BUTANE }
        if (uiState.brand.isNotEmpty()) {
            butaneBottles = butaneBottles.filter { it.company.name == uiState.brand }
        }
        if (butaneBottles.isEmpty()) {
            GazSize.entries.map { "${it.size} kg" }
        } else {
            // Trié par taille numérique pour un affichage propre (ex: 6kg, 12.5kg)
            butaneBottles.sortedBy { it.gazSize.size }.map { "${it.gazSize.size} kg" }.distinct()
        }
    }

    // ── 3. Interception de la sélection pour trouver le UUID unique de la bouteille ──
    val handleBrandChange: (String) -> Unit = { newBrand ->
        viewModel.onBrandChange(newBrand)
        updateMatchingBottleUuid(newBrand, uiState.capacityKg, uiState.availableBottles, viewModel)
    }

    val handleCapacityChange: (String) -> Unit = { newCapacity ->
        viewModel.onCapacityChange(newCapacity)
        updateMatchingBottleUuid(uiState.brand, newCapacity, uiState.availableBottles, viewModel)
    }

    // 🔥 STRATÉGIE DE SÉCURITÉ : Si la liste des bouteilles arrive APRÈS le chargement du profil,
    // on force un recalcul automatique pour ne pas bloquer le battleUuid au démarrage
    LaunchedEffect(uiState.availableBottles, uiState.brand, uiState.capacityKg) {
        if (uiState.brand.isNotBlank() && uiState.capacityKg.isNotBlank() && uiState.battleUuid.isBlank()) {
            updateMatchingBottleUuid(uiState.brand, uiState.capacityKg, uiState.availableBottles, viewModel)
        }
    }

    LaunchedEffect(uiState.isSavedProfilSuccess) {
        if (uiState.isSavedProfilSuccess) onSaved()
    }

    GazProfileContent(
        uiState          = uiState,
        distributorOptions = distributorOptions, // 🔥 Passé à la vue
        weightOptions      = weightOptions,      // 🔥 Passé à la vue
        onBackClick      = onBackClick,
        onCapacityChange = handleCapacityChange,
        onBrandChange    = handleBrandChange,
        onLocationChange = viewModel::onLocationChange,
        onRegionChange   = viewModel::onRegionChange,
        onVilleChange    = viewModel::onVilleChange,
        onQuartierChange = viewModel::onQuartierChange,
        onLieuDitChange  = viewModel::onLieuDitChange,
        onDetectGps      = onDetectGps,
        onSave           = { viewModel.saveProfile() }
    )
}

private fun updateMatchingBottleUuid(
    brand: String,
    capacityStr: String,
    bottles: List<GazBottle>,
    viewModel: GazProfileViewModel
) {
    if (brand.isNotBlank() && capacityStr.isNotBlank()) {
        // ✅ Extraction en Float? pour correspondre au type de GazSize.size
        val numericSize = capacityStr.replace(" kg", "")
            .replace(",", ".") // Sécurité au cas où le séparateur est une virgule
            .trim()
            .toFloatOrNull()

        if (numericSize != null) {
            val matchedBottle = bottles.find {
                it.gazType == GazType.BUTANE &&
                        it.company.name == brand &&
                        it.gazSize.size == numericSize
            }

            if (matchedBottle != null) {
                viewModel.onBottleSelected(matchedBottle.uuid)
            }
        }
    }
}

@Composable
private fun GazProfileContent(
    uiState          : GazProfileUiState,
    distributorOptions : List<String>,
    weightOptions      : List<String>,
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Box(modifier = Modifier.statusBarsPadding()) {
                GazProfileHeader(onBackClick = onBackClick)
            }
        },
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                GazProfileSaveButton(
                    isSaving  = uiState.isLoading,
                    isEnabled = uiState.isFormValid && !uiState.isLoading,
                    onSave    = onSave
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            GazProfileFormCard(
                uiState          = uiState,
                distributorOptions = distributorOptions,
                weightOptions      = weightOptions,
                onCapacityChange = onCapacityChange,
                onBrandChange    = onBrandChange,
                onLocationChange = onLocationChange,
                onRegionChange   = onRegionChange,
                onVilleChange    = onVilleChange,
                onQuartierChange = onQuartierChange,
                onLieuDitChange  = onLieuDitChange,
                onDetectGps      = onDetectGps
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
    }
}

