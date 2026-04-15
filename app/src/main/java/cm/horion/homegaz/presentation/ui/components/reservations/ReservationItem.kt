package cm.horion.homegaz.presentation.ui.components.reservations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.domain.model.reservation.Reservation
import cm.horion.homegaz.domain.model.reservation.ReservationStatus


@Composable
fun ReservationItem(res: Reservation, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- CÔTÉ GAUCHE : INFOS ---
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = "ID: ${res.id}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Simulation du logo de la marque (Tradex ou SCTM)
                    Box(
                        Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (res.brand == "Tradex") Color(0xFFE64A19) else Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(res.brand.take(1), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${res.brand} - ${res.weight}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(8.dp))
                StatusBadge(res.status)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "${res.price} FCFA",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            }

            // --- CÔTÉ DROIT : VISUEL ---
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                if (res.status == ReservationStatus.DELIVERING) {
                    TrackingSnippet(res.estimatedTime ?: "-- min")
                    Text(
                        text = "${res.price} FCFA",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else if (res.status == ReservationStatus.COMPLETED) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(45.dp),
                        tint = Color(0xFFBDC3C7) // Gris clair comme sur l'image
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${res.price} FCFA",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TrackingSnippet(time: String) {
    Box(
        modifier = Modifier
            .size(width = 130.dp, height = 85.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF0F4F8)) // Fond de carte léger
    ) {
        // Dessin du tracé de la route
        Canvas(modifier = Modifier.fillMaxSize()) {
            val start = Offset(20f, size.height * 0.7f)
            val end = Offset(size.width - 40f, size.height * 0.4f)

            // Ligne de route
            drawLine(
                color = Color(0xFF2C3E50),
                start = start,
                end = end,
                strokeWidth = 3f
            )

            // Points (Dépôt et Position)
            drawCircle(color = Color(0xFF2980B9), radius = 6f, center = start)
            drawCircle(color = Color(0xFF2980B9), radius = 6f, center = end)
        }

        // Textes sur la carte
        Text("Dépôt", Modifier.align(Alignment.BottomStart).padding(start = 4.dp, bottom = 4.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text("Votre Position", Modifier.align(Alignment.BottomEnd).padding(end = 4.dp, bottom = 4.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold)

        // Badge Temps
        Surface(
            modifier = Modifier.align(Alignment.TopEnd).padding(6.dp),
            shape = RoundedCornerShape(6.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Text(time, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
        }

        // Petite icône de camion
        Text("🚚", modifier = Modifier.align(Alignment.Center).offset(x = 10.dp, y = (-10).dp), fontSize = 20.sp)
    }
}

@Composable
fun StatusBadge(status: ReservationStatus) {
    val (bgColor, textColor, label) = when (status) {
        ReservationStatus.DELIVERING -> Triple(Color(0xFF2ECC71), Color.White, "EN LIVRAISON")
        ReservationStatus.COMPLETED -> Triple(Color(0xFFBDC3C7), Color.White, "TERMINÉ")
        ReservationStatus.PENDING -> Triple(Color(0xFF3498DB), Color.White, "EN ATTENTE")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
    }
}
