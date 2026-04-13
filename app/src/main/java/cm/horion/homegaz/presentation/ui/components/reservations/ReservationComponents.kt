package cm.horion.homegaz.presentation.ui.components.reservations


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.domain.model.reservation.Reservation
import cm.horion.homegaz.domain.model.reservation.ReservationStatus

@Composable
fun StatusHeader(active: Int, completed: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$active", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("En cours", style = MaterialTheme.typography.labelMedium)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$completed", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Terminées", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun ReservationItem(res: Reservation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ID: ${res.id}", style = MaterialTheme.typography.labelSmall)
                StatusBadge(res.status)
            }
            Spacer(Modifier.height(8.dp))
            Text("${res.brand} - ${res.weight}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("${res.price} FCFA", color = MaterialTheme.colorScheme.primary)

            if (res.status == ReservationStatus.DELIVERING) {
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text("Arrivée prévue : ${res.estimatedTime}", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun StatusBadge(status: ReservationStatus) {
    val color = when(status) {
        ReservationStatus.DELIVERING -> Color(0xFF4CAF50)
        ReservationStatus.PENDING -> Color(0xFFFF9800)
        ReservationStatus.COMPLETED -> Color.Gray
        else -> Color.Red
    }
    Surface(color = color.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small) {
        Text(status.label, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = color, style = MaterialTheme.typography.labelSmall)
    }
}