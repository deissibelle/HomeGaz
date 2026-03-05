package cm.horion.homegaz.presentation.ui.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.domain.model.home.DistributionPoint
import cm.horion.homegaz.presentation.ui.components.common.HomeGazButton
import java.text.NumberFormat
import java.util.Locale


@Composable
fun DistributionPointSheet(
    point: DistributionPoint,
    onNavigateClick: () -> Unit = {},
    onOrderClick: () -> Unit = {}
) {
    val formattedPrice = remember(point.priceXaf) {
        NumberFormat.getNumberInstance(Locale.FRANCE).format(point.priceXaf) + " FCFA"
    }
    val formattedDistance = remember(point.distanceKm) {
        if (point.distanceKm > 0) "À %.1f km de vous".format(point.distanceKm)
        else "Distance inconnue"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pill handle
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .padding(bottom = 8.dp)
        )

        Text(
            text = point.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = formattedPrice,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(text = formattedDistance, color = Color.Gray, fontSize = 14.sp)
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateClick,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Itinéraire")
            }

            HomeGazButton(
                text = "Commander",
                onClick = onOrderClick,
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ShoppingCart
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}