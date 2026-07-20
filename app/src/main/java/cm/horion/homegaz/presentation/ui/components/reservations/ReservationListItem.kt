package cm.horion.homegaz.presentation.ui.components.reservations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.order.dto.Order
import cm.horion.homegaz.domain.model.order.dto.OrderState
import cm.horion.homegaz.presentation.ui.theme.homeGazColors
import cm.horion.homegaz.util.getDateOnly
import cm.horion.homegaz.util.getTimeOnly

@Composable
fun ReservationListItem(
    res     : Order,
    company : String = "",
    weight  : String = "",
    onClick : () -> Unit,
) {
    val colors = MaterialTheme.homeGazColors

    // 🎯 Choix du badge de statut basé à 100% sur homeGazColors
    val (statusBg, statusContentColor, statusLabel) = when (res.orderState) {
        OrderState.STARTING -> Triple(
            colors.pendingBg,
            colors.pendingOnBg,
            stringResource(R.string.res_status_starting_label)
        )

        OrderState.LOADING -> Triple(
            colors.deliveringBg,
            colors.deliveringOnBg,
            stringResource(R.string.res_status_loading_label)
        )

        OrderState.SENDING -> Triple(
            colors.deliveringBg,
            colors.deliveringOnBg,
            stringResource(R.string.res_status_sending_label)
        )

        OrderState.SHIPPING -> Triple(
            colors.deliveringBg,
            colors.deliveringOnBg,
            stringResource(R.string.res_status_shipping_label)
        )

        OrderState.DELIVERED -> Triple(
            colors.deliveringBg, // Remplacé par le vert sémantique de livraison
            colors.deliveringOnBg,
            stringResource(R.string.res_status_delivered_label)
        )

        OrderState.ENDING -> Triple(
            colors.completedBg,
            colors.completedOnBg,
            stringResource(R.string.res_status_completed_label)
        )

        OrderState.CANCELLED -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            stringResource(R.string.res_status_cancelled_label)
        )
    }

    val title = if (weight.isNotBlank()) {
        "${company.ifBlank { "Compagnie Inconnue" }}-$weight"
    } else {
        company.ifBlank { "Compagnie Inconnue" }
    }

    val dateLabel = buildString {
        append(res.createdAt.getDateOnly())
        res.createdAt.getTimeOnly()?.let { append("  $it") }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button) { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Ligne 1 : pastille de couleur issue du thème + Marque-Poids + Date
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(3.dp))
                    // On utilise la couleur primaire ou secondaire du thème pour harmoniser les indicateurs
                    .background(MaterialTheme.colorScheme.secondary)
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text  = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                ),
                color    = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text  = dateLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Ligne 2 : badge de statut aligné à droite
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = statusBg,
            ) {
                Text(
                    text     = statusLabel,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    style    = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize   = 12.sp,
                    ),
                    color = statusContentColor,
                )
            }
        }
    }

    HorizontalDivider(
        color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        thickness = 0.6.dp,
    )
}