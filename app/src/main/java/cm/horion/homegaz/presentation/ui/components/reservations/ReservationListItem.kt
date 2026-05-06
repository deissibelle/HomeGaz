package cm.horion.homegaz.presentation.ui.components.reservations

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.reservation.Reservation
import cm.horion.homegaz.domain.model.reservation.ReservationStatus
import cm.horion.homegaz.presentation.ui.theme.homeGazColors



@Composable
fun ReservationListItem(
    res     : Reservation,
    onClick : () -> Unit,
) {
    val colors = MaterialTheme.homeGazColors

    val (statusBg, statusLabel) = when (res.status) {
        ReservationStatus.DELIVERING -> colors.deliveringBg  to stringResource(R.string.res_status_delivering_label)
        ReservationStatus.PENDING    -> colors.pendingBg     to stringResource(R.string.res_status_pending_label)
        ReservationStatus.COMPLETED  -> colors.completedBg   to stringResource(R.string.res_status_completed_label)
    }

    val delayText = res.estimatedTime
        ?.let  { stringResource(R.string.res_list_delay_label, it) }
        ?: stringResource(R.string.res_list_no_delay)

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable(role = Role.Button, onClickLabel = res.id) { onClick() },
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            //  Ligne 1 : identifiant + option + date
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text       = stringResource(R.string.res_list_title_format, res.id, res.brand),
                    style      = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                    ),
                    color    = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text  = "[${res.deliveryOption}]  ${res.date}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize   = 11.sp,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            //  Ligne 2 : statut + délais
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Icône refresh circulaire
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(

                        text  = "↻",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(Modifier.width(6.dp))

                Text(
                    text  = stringResource(R.string.res_list_status_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(Modifier.width(8.dp))

                // Badge statut
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusBg,
                ) {
                    Text(
                        text     = statusLabel,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        style    = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 11.sp,
                        ),
                        color = Color.White,
                    )
                }

                Spacer(Modifier.weight(1f))

                // Délais
                Text(
                    text  = "⏱  $delayText",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}