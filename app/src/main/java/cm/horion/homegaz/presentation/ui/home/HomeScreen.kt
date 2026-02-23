@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package cm.horion.homegaz.presentation.ui.home

import android.Manifest
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.DistributionPoint
import cm.horion.homegaz.domain.UiMarker
import cm.horion.homegaz.presentation.ui.components.BottomNavBar
import cm.horion.homegaz.presentation.ui.theme.HomeGazTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

// Données
val markers = listOf(
    UiMarker(299f, 185f, 31f, 38f),
    UiMarker(19f, 249f, 31f, 38f),
    UiMarker(168f, 223f, 42f, 51f),
    UiMarker(210f, 304f, 54f, 65f),
    UiMarker(14f, 445f, 42f, 51f),
    UiMarker(25f, 585f, 31f, 37f),
    UiMarker(181f, 604f, 31f, 37f),
    UiMarker(123f, 688f, 31f, 37f),
    UiMarker(255f, 695f, 31f, 37f),
)

private val DISTRIBUTOR_OPTIONS = listOf("SCTM", "Tradex", "Total")
private val DISTANCE_OPTIONS = listOf("100 m", "500 m", "1 km")
private val WEIGHT_OPTIONS = listOf("6kg", "12kg", "38kg")

@Composable
fun HomeScreen() {
    var selectedDistributor by remember { mutableStateOf("SCTM") }
    var selectedDistance by remember { mutableStateOf("100 m") }
    var selectedWeight by remember { mutableStateOf("12kg") }
    var selectedPoint by remember { mutableStateOf<DistributionPoint?>(null) }

    // Sécurité Preview pour les permissions
    val isPreview = LocalInspectionMode.current
    val permissionState = if (!isPreview) {
        rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    } else null

    Scaffold(
        bottomBar = { BottomNavBar() }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            // 1. LA CARTE
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val screenWidth = maxWidth
                val screenHeight = maxHeight

                Image(
                    painter = painterResource(R.drawable.map_vide),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                markers.forEach { marker ->
                    val xPos = screenWidth * (marker.x / 393f)
                    val yPos = screenHeight * (marker.y / 852f)
                    Image(
                        painter = painterResource(R.drawable.marker),
                        contentDescription = null,
                        modifier = Modifier
                            .offset(x = xPos, y = yPos)
                            .size(screenWidth * (marker.width / 393f), screenHeight * (marker.height / 852f))
                            .clickable {
//                                selectedPoint = DistributionPoint("Point de vente", 0.0, 0.0)
                            }
                    )
                }
                Box(modifier = Modifier.align(Alignment.Center)) {
                    UserMarker()
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        CustomDropdown(selectedDistributor, DISTRIBUTOR_OPTIONS, Modifier.weight(1f)) { selectedDistributor = it }
                        Spacer(Modifier.width(8.dp))
                        CustomDropdown(selectedDistance, DISTANCE_OPTIONS, Modifier.weight(1f)) { selectedDistance = it }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        CustomDropdown(selectedWeight, WEIGHT_OPTIONS, Modifier.weight(1f)) { selectedWeight = it }
                        Spacer(Modifier.width(12.dp))
                        Button(
                            onClick = { /* Actualiser */ },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366))
                        ) {
                            Text("Actualiser", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
    if (selectedPoint != null) {
        ModalBottomSheet(onDismissRequest = { selectedPoint = null }) {
            DistributionPointSheet(selectedPoint!!)
        }
    }
}

@Composable
fun CustomDropdown(selected: String, options: List<String>, modifier: Modifier, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(48.dp).clickable { expanded = true },
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF7F7F7),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = selected, fontSize = 14.sp)
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelect(option); expanded = false })
            }
        }
    }
}

@Composable
fun UserMarker() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .border(3.dp, Color(0xFF00BCD4), CircleShape) // Bordure cyan comme sur l'image
                .padding(3.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Image(
                painter = painterResource(R.drawable.profil),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Box(modifier = Modifier.width(4.dp).height(12.dp).background(Color(0xFF003366)))
    }
}

@Composable
fun DistributionPointSheet(point: DistributionPoint) {
    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(point.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Itinéraire") }
            Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366))) {
                Text("Réserver", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeGazTheme { HomeScreen() }
}