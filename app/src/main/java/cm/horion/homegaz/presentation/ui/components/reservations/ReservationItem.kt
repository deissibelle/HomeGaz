package cm.horion.homegaz.presentation.ui.components.reservations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import cm.horion.homegaz.presentation.ui.theme.homeGazColors

@Composable
fun ReservationItem(res: Reservation, onClick: () -> Unit) {
    val colors = MaterialTheme.homeGazColors

    val borderColor = when (res.status) {
        ReservationStatus.DELIVERING -> colors.deliveringBorder
        ReservationStatus.PENDING    -> colors.pendingBorder
        ReservationStatus.COMPLETED  -> colors.completedBorder
    }

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(borderColor),
            )

            Row(
                modifier          = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1.2f)) {
                    Text(
                        text       = "ID: ${res.id}",
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier          = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (res.brand == "Tradex")
                                        MaterialTheme.colorScheme.error
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                ),
                            contentAlignment  = Alignment.Center,
                        ) {
                            Text(
                                text       = res.brand.take(1),
                                color      = MaterialTheme.colorScheme.surface,
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text       = "${res.brand} - ${res.weight}",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    StatusBadge(res.status)

                    // Méthode de paiement
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(colors.orangeMoney),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text     = res.paymentMethod,
                            fontSize = 11.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text       = "${res.price} FCFA",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = MaterialTheme.colorScheme.onSurface,
                    )
                }

                Column(
                    modifier             = Modifier.weight(1f),
                    horizontalAlignment  = Alignment.End,
                ) {
                    when (res.status) {
                        ReservationStatus.DELIVERING -> {
                            TrackingSnippet(res.estimatedTime ?: "-- min")
                            Text(
                                text       = "${res.price} FCFA",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.onSurface,
                                modifier   = Modifier.padding(top = 8.dp),
                            )
                        }
                        ReservationStatus.COMPLETED -> {
                            Icon(
                                imageVector        = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier           = Modifier.size(45.dp),
                                tint               = colors.completedBg,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text       = "${res.price} FCFA",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        ReservationStatus.PENDING -> {
                            Box(
                                modifier         = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(text = "⏳", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text       = "${res.price} FCFA",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackingSnippet(time: String) {
    val mapBg = MaterialTheme.homeGazColors.mapBg

    Box(
        modifier = Modifier
            .size(width = 130.dp, height = 85.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(mapBg),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val start = Offset(20f, size.height * 0.7f)
            val end   = Offset(size.width - 40f, size.height * 0.4f)
            drawLine(color = Color(0xFF2C3E50), start = start, end = end, strokeWidth = 3f)
            drawCircle(color = Color(0xFF2980B9), radius = 6f, center = start)
            drawCircle(color = Color(0xFF2980B9), radius = 6f, center = end)
        }

        Text(
            "Dépôt",
            modifier   = Modifier.align(Alignment.BottomStart).padding(start = 4.dp, bottom = 4.dp),
            fontSize   = 9.sp,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            "Votre Position",
            modifier   = Modifier.align(Alignment.BottomEnd).padding(end = 4.dp, bottom = 4.dp),
            fontSize   = 9.sp,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onSurface,
        )

        Surface(
            modifier       = Modifier.align(Alignment.TopEnd).padding(6.dp),
            shape          = RoundedCornerShape(6.dp),
            color          = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
        ) {
            Text(
                time,
                modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                fontSize   = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = MaterialTheme.colorScheme.onSurface,
            )
        }

        Text(
            "🚚",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 10.dp, y = (-10).dp),
            fontSize = 20.sp,
        )
    }
}

@Composable
fun StatusBadge(status: ReservationStatus) {
    val colors = MaterialTheme.homeGazColors
    val (bg, fg, label) = when (status) {
        ReservationStatus.DELIVERING -> Triple(colors.deliveringBg,  colors.deliveringOnBg,  "EN LIVRAISON")
        ReservationStatus.COMPLETED  -> Triple(colors.completedBg,   colors.completedOnBg,   "TERMINÉ")
        ReservationStatus.PENDING    -> Triple(colors.pendingBg,      colors.pendingOnBg,     "EN ATTENTE")
    }

    Surface(color = bg, shape = RoundedCornerShape(6.dp)) {
        Text(
            text       = label,
            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize   = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = fg,
        )
    }
}