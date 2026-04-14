package cm.horion.homegaz.presentation.ui.components.reservations

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.domain.model.reservation.Reservation
import cm.horion.homegaz.domain.model.reservation.ReservationStatus


@Composable
fun StatusHeader(active: Int, completed: Int) {
    val total = active + completed
    val activeAngle = if (total > 0) (active.toFloat() / total) * 270f else 0f
    val completedAngle = if (total > 0) (completed.toFloat() / total) * 270f else 0f

    // Animation d'entrée de l'arc
    val animatedActive by animateFloatAsState(
        targetValue = activeAngle,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "activeArc"
    )
    val animatedCompleted by animateFloatAsState(
        targetValue = completedAngle,
        animationSpec = tween(1000, delayMillis = 200, easing = EaseOutCubic),
        label = "completedArc"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F1FB)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Arc circulaire
            CircularArcStat(
                activeAngle = animatedActive,
                completedAngle = animatedCompleted,
                activeCount = active,
                size = 90.dp
            )

            Spacer(Modifier.width(20.dp))

            // Stats textuelles
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StatRow(
                    count = active,
                    label = "En Cours",
                    color = Color(0xFF1A56DB)
                )
                StatRow(
                    count = completed,
                    label = "Terminé",
                    color = Color(0xFF9CA3AF)
                )
            }

            Spacer(Modifier.weight(1f))

            // Label "Global" + icônes
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Global",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionIconButton(icon = { Icon(Icons.Default.BarChart, null, Modifier.size(16.dp)) })
                    ActionIconButton(
                        icon = { Icon(Icons.Default.CalendarMonth, null, Modifier.size(16.dp)) },
                        containerColor = Color(0xFF1A56DB),
                        iconTint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun CircularArcStat(
    activeAngle: Float,
    completedAngle: Float,
    activeCount: Int,
    size: Dp
) {
    val activeColor = Color(0xFF1A56DB)
    val completedColor = Color(0xFF9CA3AF)
    val trackColor = Color(0xFFDDE6F0)
    val strokeWidth = 10.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size)
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            val startAngle = 135f
            val sweepTotal = 270f

            // Track gris
            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = sweepTotal,
                useCenter = false,
                style = stroke
            )
            // Arc terminé (gris)
            if (completedAngle > 0f) {
                drawArc(
                    color = completedColor,
                    startAngle = startAngle,
                    sweepAngle = completedAngle,
                    useCenter = false,
                    style = stroke
                )
            }
            // Arc actif (bleu) par-dessus
            if (activeAngle > 0f) {
                drawArc(
                    color = activeColor,
                    startAngle = startAngle + completedAngle,
                    sweepAngle = activeAngle,
                    useCenter = false,
                    style = stroke
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "$activeCount",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "En Cours",
                fontSize = 9.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
private fun StatRow(count: Int, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            "$count $label",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ActionIconButton(
    icon: @Composable () -> Unit,
    containerColor: Color = Color(0xFFD1E3F8),
    iconTint: Color = Color(0xFF1A56DB)
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        modifier = Modifier.size(32.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            CompositionLocalProvider(LocalContentColor provides iconTint) {
                icon()
            }
        }
    }
}



@Composable
fun ReservationSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        placeholder = {
            Text(
                "Rechercher une réservation",
                color = Color(0xFF9CA3AF),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(18.dp)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color(0xFFE5E7EB),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
    )
}



@Composable
fun ReservationItem(res: Reservation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Colonne gauche : infos
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // ID
                Text(
                    "ID: ${res.id}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Logo marque + nom
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BrandLogo(brand = res.brand)
                    Text(
                        res.brand,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Poids
                Text(
                    res.weight,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )

                // Badge statut
                StatusBadge(res.status)

                // Prix
                Text(
                    "${res.price} FCFA",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Colonne droite : mini-map ou icône check
            Box(
                modifier = Modifier
                    .size(width = 110.dp, height = 100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                when (res.status) {
                    ReservationStatus.DELIVERING -> DeliveryMiniMap(estimatedTime = res.estimatedTime ?: "")
                    ReservationStatus.COMPLETED  -> CompletedIcon()
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun DeliveryMiniMap(estimatedTime: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0EEE4))
    ) {
        // Ligne de route simulée
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(size.width * 0.15f, size.height * 0.6f)
                cubicTo(
                    size.width * 0.3f, size.height * 0.4f,
                    size.width * 0.6f, size.height * 0.55f,
                    size.width * 0.85f, size.height * 0.5f
                )
            }
            drawPath(path, color = Color(0xFF1A56DB), style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))

            // Point départ (bleu)
            drawCircle(Color(0xFF1A56DB), radius = 5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.6f))
            // Point destination (bleu)
            drawCircle(Color(0xFF1A56DB), radius = 5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.5f))
        }

        // Label "Dépôt"
        Text(
            "Dépôt",
            fontSize = 8.sp,
            color = Color(0xFF374151),
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 6.dp, top = 4.dp)
        )
        // Label "Votre Position"
        Text(
            "Votre Position",
            fontSize = 8.sp,
            color = Color(0xFF374151),
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 4.dp, bottom = 4.dp)
        )

        // Badge temps
        Surface(
            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
            shape = RoundedCornerShape(6.dp),
            color = Color.White
        ) {
            Text(
                estimatedTime,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF374151),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        // Icône camion
        Text(
            "🚚",
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

// Icône check pour les commandes terminées
@Composable
private fun CompletedIcon() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFFD1D5DB),
            modifier = Modifier.size(52.dp)
        )
    }
}

@Composable
private fun BrandLogo(brand: String) {
    val (bgColor, textColor) = when (brand.uppercase()) {
        "SCTM"   -> Color(0xFFFF6B35) to Color.White
        "TRADEX" -> Color(0xFFEF4444) to Color.White
        "TOTAL"  -> Color(0xFFEF4444) to Color.White
        else     -> Color(0xFF6B7280) to Color.White
    }
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            brand.take(2).uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}



@Composable
fun StatusBadge(status: ReservationStatus) {
    val (bgColor, textColor) = when (status) {
        ReservationStatus.DELIVERING -> Color(0xFF10B981) to Color.White
        ReservationStatus.PENDING    -> Color(0xFFFEF3C7) to Color(0xFFD97706)
        ReservationStatus.COMPLETED  -> Color(0xFFF3F4F6) to Color(0xFF6B7280)
        ReservationStatus.CONFIRMED  -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
        ReservationStatus.CANCELLED  -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
    }
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Text(
            text = status.label.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = 0.5.sp
        )
    }
}