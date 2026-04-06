package cm.horion.homegaz.presentation.ui.pages.gazprofile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cm.horion.homegaz.R
import cm.horion.homegaz.presentation.state.GazProfileUiState
import cm.horion.homegaz.presentation.viewmodel.GazProfileViewModel
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel


@Composable
fun GazProfileScreen(
    onBackClick : () -> Unit = {},
    onSaved     : () -> Unit = {},
    viewModel   : GazProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onSaved()
    }

    GazProfileContent(
        uiState             = uiState,
        onBackClick         = onBackClick,
        onCapacityChange    = viewModel::onCapacityChange,
        onBrandChange       = viewModel::onBrandChange,
        onLocationChange    = viewModel::onLocationChange,
        onConsumptionChange = viewModel::onConsumptionChange,
        onPhotoUriChange    = viewModel::onPhotoUriChange,
        onNotesChange       = viewModel::onNotesChange,
        onSave              = { viewModel.saveProfile(onSaved) }
    )
}


@Composable
private fun GazProfileContent(
    uiState             : GazProfileUiState,
    onBackClick         : () -> Unit,
    onCapacityChange    : (String) -> Unit,
    onBrandChange       : (String) -> Unit,
    onLocationChange    : (String) -> Unit,
    onConsumptionChange : (String) -> Unit,
    onPhotoUriChange    : (String?) -> Unit,
    onNotesChange       : (String) -> Unit,
    onSave              : () -> Unit
) {
    val capacityOptions = listOf(
        stringResource(R.string.gaz_profile_capacity_6),
        stringResource(R.string.gaz_profile_capacity_12),
        stringResource(R.string.gaz_profile_capacity_38)
    )
    val consumptionOptions = listOf(
        stringResource(R.string.gaz_profile_consumption_half),
        stringResource(R.string.gaz_profile_consumption_1),
        stringResource(R.string.gaz_profile_consumption_2),
        stringResource(R.string.gaz_profile_consumption_3)
    )

    // Launcher pour choisir une photo dans la galerie
    val photoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> onPhotoUriChange(uri?.toString()) }

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
                // En-tête
                ProfileHeader(onBackClick = onBackClick)

                Spacer(modifier = Modifier.height(20.dp))

                // Carte formulaire
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Capacité
                    ProfileDropdownField(
                        icon    = Icons.Outlined.Scale,
                        label   = stringResource(R.string.gaz_profile_capacity),
                        options = capacityOptions,
                        selected = uiState.capacityKg,
                        onSelect = onCapacityChange,
                        isRequired = true
                    )

                    // Marque
                    ProfileTextField(
                        icon       = Icons.Outlined.LocalOffer,
                        label      = stringResource(R.string.gaz_profile_brand),
                        hint       = stringResource(R.string.gaz_profile_brand_hint),
                        value      = uiState.brand,
                        onValueChange = onBrandChange,
                        isRequired = true
                    )

                    // Lieu d'utilisation
                    ProfileLocationField(
                        value         = uiState.usageLocation,
                        onValueChange = onLocationChange,
                        isRequired    = true
                    )

                    // Consommation
                    ProfileDropdownField(
                        icon     = Icons.Outlined.BarChart,
                        label    = stringResource(R.string.gaz_profile_consumption),
                        options  = consumptionOptions,
                        selected = uiState.consumption,
                        onSelect = onConsumptionChange,
                        isRequired = true
                    )



                    // Notes (optionnel)
                    ProfileNotesField(
                        value         = uiState.notes,
                        onValueChange = onNotesChange
                    )
                }

                // Erreur globale
                uiState.errorMessage?.let { msg ->
                    Text(
                        text  = msg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Button(
                onClick  = onSave,
                enabled  = uiState.isFormValid && !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        color    = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector        = Icons.Filled.Save,
                        contentDescription = null,
                        modifier           = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text       = stringResource(R.string.gaz_profile_save),
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 16.sp
                    )
                }
            }
        }
    }
}


@Composable
private fun ProfileHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Bouton retour
        IconButton(
            onClick  = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint               = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 56.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text       = stringResource(R.string.gaz_profile_title),
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color      = MaterialTheme.colorScheme.primary
            )
            Text(
                text  = stringResource(R.string.gaz_profile_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}


@Composable
private fun FieldRow(
    icon    : ImageVector,
    label   : String,
    optional: Boolean = false,
    content : @Composable ColumnScope.() -> Unit
) {
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(22.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text       = label,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color      = MaterialTheme.colorScheme.onBackground
                )
                if (optional) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text  = stringResource(R.string.gaz_profile_photo_optional),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            content()
        }
    }
}


@Composable
private fun ProfileTextField(
    icon          : ImageVector,
    label         : String,
    hint          : String,
    value         : String,
    onValueChange : (String) -> Unit,
    isRequired    : Boolean = false
) {
    FieldRow(icon = icon, label = label) {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            placeholder   = { Text(hint, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(10.dp),
            colors        = outlinedFieldColors()
        )
    }
}


@Composable
private fun ProfileLocationField(
    value         : String,
    onValueChange : (String) -> Unit,
    isRequired    : Boolean = false
) {
    FieldRow(icon = Icons.Outlined.LocationOn, label = stringResource(R.string.gaz_profile_location)) {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            placeholder   = {
                Text(
                    stringResource(R.string.gaz_profile_location_hint),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(10.dp),
            trailingIcon  = {
                IconButton(onClick = {  }) {
                    Icon(
                        imageVector = Icons.Outlined.MyLocation,
                        contentDescription = stringResource(R.string.gaz_profile_location_detect),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = outlinedFieldColors()
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileDropdownField(
    icon       : ImageVector,
    label      : String,
    options    : List<String>,
    selected   : String,
    onSelect   : (String) -> Unit,
    isRequired : Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    FieldRow(icon = icon, label = label) {
        ExposedDropdownMenuBox(
            expanded         = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value         = selected,
                onValueChange = {},
                readOnly      = true,
                placeholder   = { Text("Sélectionner", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier      = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape         = RoundedCornerShape(10.dp),
                colors        = outlinedFieldColors()
            )
            ExposedDropdownMenu(
                expanded         = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text    = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun ProfilePhotoField(
    photoUri    : String?,
    onPickPhoto : () -> Unit,
    onRemovePhoto: () -> Unit
) {
    FieldRow(
        icon     = Icons.Outlined.CameraAlt,
        label    = stringResource(R.string.gaz_profile_photo),
        optional = true
    ) {
        if (photoUri != null) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model             = photoUri,
                    contentDescription = stringResource(R.string.gaz_profile_photo_desc),
                    contentScale      = ContentScale.Crop,
                    modifier          = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                IconButton(
                    onClick  = onRemovePhoto,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Close,
                        contentDescription = "Supprimer la photo",
                        tint               = MaterialTheme.colorScheme.error
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onPickPhoto() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = stringResource(R.string.gaz_profile_photo_add),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


@Composable
private fun ProfileNotesField(
    value         : String,
    onValueChange : (String) -> Unit
) {
    FieldRow(
        icon     = Icons.Outlined.Edit,
        label    = stringResource(R.string.gaz_profile_notes),
        optional = true
    ) {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            placeholder   = {
                Text(
                    stringResource(R.string.gaz_profile_notes_hint),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(10.dp),
            colors   = outlinedFieldColors()
        )
    }
}


@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    focusedLabelColor    = MaterialTheme.colorScheme.primary,
    cursorColor          = MaterialTheme.colorScheme.primary
)

@Preview(showBackground = true)
@Composable
fun GazProfileScreenPreview(modifier: Modifier = Modifier) {
    GazProfileScreen()
}