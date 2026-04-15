package cm.horion.homegaz.presentation.ui.components.reservations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.presentation.ui.theme.HG_Blue_Header_Bg
import cm.horion.homegaz.presentation.ui.theme.HG_Blue_Indicator
import cm.horion.homegaz.presentation.ui.theme.HG_Text_Dark_Header

@Composable
fun StatusHeader(active: Int, completed: Int) {
    val total = (active + completed).toFloat().coerceAtLeast(1f)
    val activeSweep = (active / total) * 360f
    val completedSweep = (completed / total) * 360f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = HG_Blue_Header_Bg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(80.dp)) {
                    drawArc(color = Color(0xFFE0E0E0), startAngle = 0f, sweepAngle = 360f, useCenter = false, style = Stroke(width = 8.dp.toPx()))
                    drawArc(color = HG_Blue_Indicator, startAngle = -90f, sweepAngle = activeSweep, useCenter = false, style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round))
                    drawArc(color = Color(0xFF2E7D32), startAngle = -90f + activeSweep, sweepAngle = completedSweep, useCenter = false, style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$active", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = HG_Text_Dark_Header)
                    Text(text = stringResource(R.string.res_header_active), fontSize = 11.sp, color = HG_Text_Dark_Header)
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$completed", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = HG_Text_Dark_Header)
                Text(text = stringResource(R.string.res_header_completed), fontSize = 11.sp, color = HG_Text_Dark_Header)
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.End) {
                Row {
                    StatusIconBadge(icon = Icons.Default.BarChart, color = Color(0xFF1976D2))
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusIconBadge(icon = Icons.Default.DateRange, color = Color(0xFF4CAF50))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.res_header_global),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = HG_Text_Dark_Header
                )
            }
        }
    }
}

@Composable
fun StatusIconBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(color, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
    }
}
