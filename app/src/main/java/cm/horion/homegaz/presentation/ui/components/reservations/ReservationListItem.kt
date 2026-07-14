package cm.horion.homegaz.presentation.ui.components.reservations


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TrackChanges
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
import cm.horion.homegaz.domain.model.order.dto.Order
import cm.horion.homegaz.domain.model.order.dto.OrderState
import cm.horion.homegaz.domain.model.reservation.Reservation
import cm.horion.homegaz.domain.model.reservation.ReservationStatus
import cm.horion.homegaz.presentation.ui.theme.homeGazColors
import cm.horion.homegaz.util.getDateOnly

@Composable
fun ReservationListItem(
    res     : Order,
    company : String = "",
    onClick : () -> Unit,
) {
    val colors = MaterialTheme.homeGazColors

    // 🎯 Choix dynamique des couleurs de statut selon le thème actif
    val (statusBg, statusContentColor, statusLabel) = when (res.orderState) {
        OrderState.LOADING -> Triple(
            colors.deliveringBg,
            Color.White,
            stringResource(R.string.res_status_delivering_label)
        )
        OrderState.SENDING -> Triple(
            colors.pendingBg,
            Color.White,
            stringResource(R.string.res_status_pending_label)
        )
        OrderState.ENDING -> Triple(
            colors.completedBg,
            Color.White,
            stringResource(R.string.res_status_completed_label)
        )
        else -> Triple(
            colors.deliveringBg,
            Color.White,
            stringResource(R.string.res_status_delivering_label)
        )
    }

    val delayText = "faut retirer sa"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.Button) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Ligne 1 : Nom de la Compagnie + Date
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text  = company.ifBlank { "Compagnie Inconnue" },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                    ),
                    color = MaterialTheme.colorScheme.primary, // 🎯 Texte principal s'adapte automatiquement (Noir en clair / Blanc en sombre)
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text  = "[${res.deliveryMode}]  ${res.createdAt.getDateOnly()}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // 🎯 Gris/Neutre pour les infos secondaires
                )
            }

            // Ligne 2 : Statut (Badge) + Délai / Icône Horloge
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.TrackChanges,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text  = stringResource(R.string.res_list_status_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.width(8.dp))

                // Badge statut pro
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBg,
                ) {
                    Text(
                        text     = statusLabel,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style    = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize   = 11.sp,
                        ),
                        color = statusContentColor,
                    )
                }

                Spacer(Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text  = delayText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

//@Composable
//fun ReservationListItem(
//    res     : Order,
//    company : String = "",
//    onClick : () -> Unit,
//) {
//    val colors = MaterialTheme.homeGazColors
//
//    val (statusBg, statusLabel) = when (res.orderState) {
//        OrderState.LOADING -> colors.deliveringBg  to stringResource(R.string.res_status_delivering_label)
//        OrderState.SENDING    -> colors.pendingBg     to stringResource(R.string.res_status_pending_label)
//        OrderState.ENDING  -> colors.completedBg   to stringResource(R.string.res_status_completed_label)
//        else -> colors.deliveringBg  to stringResource(R.string.res_status_delivering_label)
//    }
//
//    val delayText = "faut retirer sa"
//
//    Card(
//        modifier  = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 5.dp)
//            .clickable(role = Role.Button, onClickLabel = res.id) { onClick() },
//        shape     = RoundedCornerShape(12.dp),
//        colors    = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface,
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
//    ) {
//        Column(
//            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp),
//        ) {
//
//            //  Ligne 1 : identifiant + option + date
//            Row(
//                modifier          = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                Text(
//                    //text       = stringResource(R.string.res_list_title_format, res.id, res.brand),
//                    text       = company,
//                    style      = MaterialTheme.typography.bodyMedium.copy(
//                        fontWeight = FontWeight.Bold,
//                        fontSize   = 15.sp,
//                    ),
//                    color    = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.weight(1f),
//                )
//                Text(
//                    text  = "[${res.deliveryMode}]  ${res.createdAt.getDateOnly()}",
//                    style = MaterialTheme.typography.labelSmall.copy(
//                        fontWeight = FontWeight.Normal,
//                        fontSize   = 11.sp,
//                    ),
//                    color = MaterialTheme.colorScheme.primary,
//                )
//            }
//
//            //  Ligne 2 : statut + délais
//            Row(
//                modifier          = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//
//                    Icon(
//                        imageVector = Icons.Outlined.TrackChanges,
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.size(12.dp)
//                    )
//
//                    Spacer(Modifier.width(6.dp))
//
//                    Text(
//                        text = stringResource(R.string.res_list_status_label),
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.primary,
//                    )
//
//                Spacer(Modifier.width(8.dp))
//
//                // Badge statut
//                Surface(
//                    shape = RoundedCornerShape(6.dp),
//                    color = statusBg,
//                ) {
//                    Text(
//                        text     = statusLabel,
//                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
//                        style    = MaterialTheme.typography.labelSmall.copy(
//                            fontWeight = FontWeight.SemiBold,
//                            fontSize   = 11.sp,
//                        ),
//                        color = Color.White,
//                    )
//                }
//
//                Spacer(Modifier.weight(1f))
//
//                Icon(
//                    imageVector = Icons.Outlined.Schedule,
//                    contentDescription = null,
//                    tint = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.size(12.dp)
//                )
//
//                Spacer(modifier = Modifier.width(4.dp))
//
//                Text(
//                    text = delayText,
//                    style = MaterialTheme.typography.labelSmall,
//                    color = MaterialTheme.colorScheme.primary,
//                )
//            }
//        }
//    }
//}