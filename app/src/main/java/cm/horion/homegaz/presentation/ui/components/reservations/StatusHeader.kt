package cm.horion.homegaz.presentation.ui.components.reservations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.presentation.ui.theme.homeGazColors

@Composable
fun StatusHeader(active: Int, completed: Int) {
    val colors    = MaterialTheme.homeGazColors
    val total     = (active + completed).toFloat().coerceAtLeast(1f)
    val activeSweep    = (active    / total) * 360f
    val completedSweep = (completed / total) * 360f

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape     = RoundedCornerShape(24.dp),
        colors    = CardDefaults.cardColors(containerColor = colors.headerBg),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Row(
            modifier          = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(80.dp)) {
                    val stroke = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    drawArc(
                        color       = colors. deliveringBg ,
                        startAngle  = 0f,
                        sweepAngle  = 360f,
                        useCenter   = false,
                        style       = Stroke(width = 8.dp.toPx()),
                    )
                    drawArc(
                        color      = colors.headerIndicator,
                        startAngle = -90f,
                        sweepAngle = activeSweep,
                        useCenter  = false,
                        style      = stroke,
                    )
                    drawArc(
                        color      = colors.success,
                        startAngle = -90f + activeSweep,
                        sweepAngle = completedSweep,
                        useCenter  = false,
                        style      = stroke,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = "$active",
                       style      = MaterialTheme.typography.bodyLarge,
                        color      = colors.headerTextDark,
                    )
                    Text(
                        text     = stringResource(R.string.res_header_active),
                        style      = MaterialTheme.typography.labelSmall,
                        color    = colors.headerTextDark,
                    )
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text       = "$completed",
                    style      = MaterialTheme.typography.bodyLarge,
                    color      = colors.headerTextDark,
                )
                Text(
                    text     = stringResource(R.string.res_header_completed),
                    style      = MaterialTheme.typography.labelSmall,
                    color    = colors.headerTextDark,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.End) {
                Row {
                    StatusIconBadge(
                        icon  = Icons.Default.BarChart,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusIconBadge(
                        icon  = Icons.Default.DateRange,
                        color = colors.success,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text       = stringResource(R.string.res_header_global),
                    style      = MaterialTheme.typography.titleLarge,
                    color      = colors.headerTextDark,
                )
            }
        }
    }
}

@Composable
fun StatusIconBadge(icon: ImageVector, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier          = Modifier
            .size(28.dp)
            .background(color, shape = CircleShape),
        contentAlignment  = Alignment.Center,
    ) {
        Icon(
            imageVector    = icon,
            contentDescription = null,
            tint           = MaterialTheme.colorScheme.onPrimary,
            modifier       = Modifier.size(16.dp),
        )
    }
}