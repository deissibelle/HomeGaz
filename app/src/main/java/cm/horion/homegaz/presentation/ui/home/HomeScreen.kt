@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package cm.horion.homegaz.presentation.ui.home


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.domain.DistributionPoint
import cm.horion.homegaz.presentation.ui.theme.HomeGazTheme
import com.google.accompanist.permissions.*





private val FAKE_POINTS = listOf(
    DistributionPoint("1", "Algo Gaz",    latitude = 3.850, longitude = 11.500),
    DistributionPoint("2", "Globus Gaz",  latitude = 3.851, longitude = 11.505),
    DistributionPoint("3", "InterCom",    latitude = 3.849, longitude = 11.508),
    DistributionPoint("4", "Optimun Gaz", latitude = 3.847, longitude = 11.512),
    DistributionPoint("5", "Comex",       latitude = 3.852, longitude = 11.510),
)

private val DISTRIBUTOR_OPTIONS = listOf("SCTM", "Tradex", "Total", "Bocom", "Globus")
private val WEIGHT_OPTIONS      = listOf("6kg", "12kg", "38kg")


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var selectedDistributor by remember { mutableStateOf(DISTRIBUTOR_OPTIONS.first()) }
    var selectedWeight      by remember { mutableStateOf(WEIGHT_OPTIONS[1]) }

    var pendingPoint  by remember { mutableStateOf<DistributionPoint?>(null) }

    var selectedPoint by remember { mutableStateOf<DistributionPoint?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val locationPermission = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) { granted ->
        if (granted) {
            selectedPoint = pendingPoint
        }
        pendingPoint = null
    }

    val onMarkerClick: (DistributionPoint) -> Unit = { point ->
        if (locationPermission.status.isGranted) {
            selectedPoint = point
        } else {
            pendingPoint = point
            locationPermission.launchPermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapPlaceholder(
            points        = FAKE_POINTS,
            onMarkerClick = onMarkerClick
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp)),
            shadowElevation = 6.dp,
            color           = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HomeGazDropdown(
                    selected = selectedDistributor,
                    options  = DISTRIBUTOR_OPTIONS,
                    onSelect = { selectedDistributor = it },
                    modifier = Modifier.weight(1f)
                )
                HomeGazDropdown(
                    selected = selectedWeight,
                    options  = WEIGHT_OPTIONS,
                    onSelect = { selectedWeight = it },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text  = "Actualiser",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
        val permStatus = locationPermission.status
        if (permStatus is PermissionStatus.Denied && !permStatus.shouldShowRationale) {
            PermissionDeniedBanner(
                onRetry  = { locationPermission.launchPermissionRequest() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
    selectedPoint?.let { point ->
        ModalBottomSheet(
            onDismissRequest = { selectedPoint = null },
            sheetState       = sheetState,
            shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            DistributionPointSheet(
                point        = point,
                onReserver   = {
                    selectedPoint = null
                },
                onItineraire = {
                    selectedPoint = null
                }
            )
        }
    }
}

@Composable
fun DistributionPointSheet(
    point        : DistributionPoint,
    onReserver   : () -> Unit,
    onItineraire : () -> Unit,
    modifier     : Modifier = Modifier
) {
    Column(
        modifier            = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // AsyncImage(
        //     model= point.imageUrl,
        //     contentDescription = point.name,
        //     contentScale = ContentScale.Crop,
        //     modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp))
        // )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text  = "Photo du point",
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text= point.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick  = onItineraire,
                modifier = Modifier.weight(1f).height(50.dp),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Text("Itinéraire", style = MaterialTheme.typography.labelLarge)
            }

            Button(
                onClick  = onReserver,
                modifier = Modifier.weight(1f).height(50.dp),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Text("Réserver", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun PermissionDeniedBanner(
    onRetry  : () -> Unit,
    modifier : Modifier = Modifier
) {
    Surface(
        modifier        = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding(),
        shape = RoundedCornerShape(12.dp),
        color= MaterialTheme.colorScheme.errorContainer,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment= Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text     = "Localisation requise\npour voir les détails",
                style    = MaterialTheme.typography.bodySmall,
                color    = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onRetry) {
                Text("Réessayer", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun MapPlaceholder(
    points        : List<DistributionPoint>,
    onMarkerClick : (DistributionPoint) -> Unit,
    modifier      : Modifier = Modifier
) {
    Box(
        modifier= modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint= MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(56.dp)
            )
            Text(
                text = "Google Maps",
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text  = "Simuler un clic marker",
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelSmall
            )
            points.forEach { point ->
                OutlinedButton(onClick = { onMarkerClick(point) }) {
                    Text(point.name, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun HomeGazDropdown(
    selected : String,
    options  : List<String>,
    onSelect : (String) -> Unit,
    modifier : Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded         = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier         = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier= Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            textStyle= MaterialTheme.typography.bodySmall,
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text    = { Text(option, style = MaterialTheme.typography.bodySmall) },
                    onClick = { onSelect(option); expanded = false }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeGazTheme {
        HomeScreen()
    }
}