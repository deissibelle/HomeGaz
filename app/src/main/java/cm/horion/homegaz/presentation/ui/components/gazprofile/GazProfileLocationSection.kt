package cm.horion.homegaz.presentation.ui.components.gazprofile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.gazprofile.CameroonData


@Composable
fun GazProfileLocationSection(
    usageLocation    : String,
    region           : String,
    ville            : String,
    quartier         : String,
    lieuDit          : String,
    onLocationChange : (String) -> Unit,
    onRegionChange   : (String) -> Unit,
    onVilleChange    : (String) -> Unit,
    onQuartierChange : (String) -> Unit,
    onLieuDitChange  : (String) -> Unit,
    onDetectGps      : () -> Unit,
) {
    val cities = remember(region) { CameroonData.getCitiesForRegion(region) }

    LaunchedEffect(region) {
        if (ville.isNotBlank() && !cities.contains(ville)) {
            onVilleChange("")
            onQuartierChange("")
        }
    }

    LaunchedEffect(ville) {
        if (quartier.isNotBlank() && ville != "Yaoundé") {
            onQuartierChange("")
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // Lieu d'utilisation
        LocationTextField(
            icon          = Icons.Outlined.LocationOn,
            label         = stringResource(R.string.gaz_profile_position),
            value         = usageLocation,
            onValueChange = onLocationChange,
            placeholder   = stringResource(R.string.gaz_profile_location_hint),
            trailingIcon  = {
                IconButton(onClick = onDetectGps) {
                    Icon(
                        imageVector        = Icons.Outlined.MyLocation,
                        contentDescription = stringResource(R.string.gaz_profile_location_detect),
                        tint               = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        // Région
        GazProfileDropdownField(
            icon     = Icons.Outlined.Map,
            label    = stringResource(R.string.gaz_profile_region),
            options  = CameroonData.REGIONS,
            selected = region,
            onSelect = onRegionChange
        )

        //  Ville
        GazProfileDropdownField(
            icon     = Icons.Outlined.LocationCity,
            label    = stringResource(R.string.gaz_profile_ville),
            options  = if (region.isBlank()) listOf() else cities,
            selected = ville,
            onSelect = onVilleChange,
            enabled  = region.isNotBlank(),
            placeholder = if (region.isBlank())
                stringResource(R.string.gaz_profile_ville_hint_select_region)
            else
                stringResource(R.string.gaz_profile_ville_hint)
        )

        // Quartier
        if (ville == "Yaoundé") {
            GazProfileDropdownField(
                icon     = Icons.Outlined.AccountBalance,
                label    = stringResource(R.string.gaz_profile_quartier),
                options  = CameroonData.YAOUNDE_QUARTERS,
                selected = quartier,
                onSelect = onQuartierChange
            )
        } else {
            LocationTextField(
                icon          = Icons.Outlined.AccountBalance,
                label         = stringResource(R.string.gaz_profile_quartier),
                value         = quartier,
                onValueChange = onQuartierChange,
                placeholder   = stringResource(R.string.gaz_profile_quartier_hint),
                enabled       = ville.isNotBlank()
            )
        }

        // Lieu-dit
        LocationTextField(
            icon          = Icons.Outlined.Signpost,
            label         = stringResource(R.string.gaz_profile_lieu_dit),
            value         = lieuDit,
            onValueChange = onLieuDitChange,
            placeholder   = stringResource(R.string.gaz_profile_lieu_dit_hint),
            optional      = true
        )
    }
}

@Composable
fun LocationTextField(
    icon          : ImageVector,
    label         : String,
    value         : String,
    onValueChange : (String) -> Unit,
    placeholder   : String,
    optional      : Boolean = false,
    enabled       : Boolean = true,
    trailingIcon  : (@Composable () -> Unit)? = null
) {
    GazProfileFieldRow(icon = icon, label = label, optional = optional) {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            enabled       = enabled,
            placeholder   = {
                Text(
                    text  = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            singleLine   = true,
            modifier     = Modifier.fillMaxWidth(),
            shape        = RoundedCornerShape(10.dp),
            trailingIcon = trailingIcon,
            colors       = gazProfileFieldColors()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GazProfileDropdownField(
    icon        : androidx.compose.ui.graphics.vector.ImageVector,
    label       : String,
    options     : List<String>,
    selected    : String,
    onSelect    : (String) -> Unit,
    enabled     : Boolean = true,
    placeholder : String  = "Sélectionner"
) {
    var expanded by remember { mutableStateOf(false) }

    GazProfileFieldRow(icon = icon, label = label) {
        ExposedDropdownMenuBox(
            expanded         = if (enabled) expanded else false,
            onExpandedChange = { if (enabled) expanded = it }
        ) {
            OutlinedTextField(
                value         = selected,
                onValueChange = {},
                readOnly      = true,
                enabled       = enabled,
                placeholder   = {
                    Text(
                        text  = placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier     = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape  = RoundedCornerShape(10.dp),
                colors = gazProfileFieldColors()
            )

            if (options.isNotEmpty()) {
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
}