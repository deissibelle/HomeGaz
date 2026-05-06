package cm.horion.homegaz.presentation.ui.pages.reservations

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.reservation.Reservation
import cm.horion.homegaz.domain.model.reservation.ReservationStatus
import cm.horion.homegaz.presentation.ui.theme.HG_Background_Light
import cm.horion.homegaz.presentation.ui.theme.homeGazColors


@Composable
fun ReservationDetailScreen(
    reservation : Reservation,
    onBackClick : () -> Unit,
) {
    val colors = MaterialTheme.homeGazColors

    val (statusBg, statusLabel) = when (reservation.status) {
        ReservationStatus.DELIVERING ->
            colors.deliveringBg to stringResource(R.string.res_detail_status_delivering)
        ReservationStatus.PENDING    ->
            colors.pendingBg    to stringResource(R.string.res_detail_status_pending)
        ReservationStatus.COMPLETED  ->
            colors.completedBg  to stringResource(R.string.res_detail_status_completed)
    }

    val headerTitle = stringResource(
        R.string.res_list_title_format,
        reservation.id,
        reservation.brand,
    )
    val headerSubtitle = buildString {
        append(reservation.date)
        reservation.time?.let { append("  $it") }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .height(56.dp),
        ) {
            IconButton(
                onClick  = onBackClick,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .semantics {
                        contentDescription = "Retour"
                    },
            ) {
                Icon(
                    imageVector        = Icons.Default.ArrowBackIosNew,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(30.dp),
                )
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text  = headerTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize   = 24.sp,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                )
                if (headerSubtitle.isNotBlank()) {
                    Text(
                        text  = headerSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(12.dp),
                )
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState()),
        ) {

            DetailRow(
                icon  = Icons.Outlined.Settings,
                label = stringResource(R.string.res_detail_label_marque),
                value = reservation.brand,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.6.dp)

            DetailRow(
                icon  = Icons.Outlined.Scale,
                label = stringResource(R.string.res_detail_label_poids),
                value = reservation.weight,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.6.dp)

            DetailRow(
                icon  = Icons.Outlined.Inventory2,
                label = stringResource(R.string.res_detail_label_quantite),
                value = reservation.quantity.toString(),
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.6.dp)

            DetailRow(
                icon  = Icons.Outlined.LocalShipping,
                label = stringResource(R.string.res_detail_label_option),
                value = reservation.deliveryOption,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.6.dp)

            DetailRow(
                icon  = Icons.Outlined.Timer,
                label = stringResource(R.string.res_detail_label_delais),
                value = reservation.estimatedTime
                    ?: stringResource(R.string.res_detail_no_delay),
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.6.dp)

            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Info,
                    contentDescription = null,
                    modifier           = Modifier.size(20.dp),
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text     = stringResource(R.string.res_detail_label_statut),
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusBg,
                ) {
                    Text(
                        text     = statusLabel,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        style    = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 13.sp,
                        ),
                        color = Color.White,
                    )
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.6.dp)

            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Payment,
                    contentDescription = null,
                    modifier           = Modifier.size(20.dp),
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text     = stringResource(R.string.res_detail_label_montant),
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text  = stringResource(R.string.res_detail_price_format, reservation.price),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize   = 15.sp,
                        ),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text  = reservation.paymentMethod,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon  : ImageVector,
    label : String,
    value : String,
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            modifier           = Modifier.size(20.dp),
            tint               = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text     = label,
            style    = MaterialTheme.typography.bodyMedium,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text  = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp,
            ),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}