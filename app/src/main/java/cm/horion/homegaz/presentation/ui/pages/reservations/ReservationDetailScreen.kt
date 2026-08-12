package cm.horion.homegaz.presentation.ui.pages.reservations

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle
import cm.horion.homegaz.domain.model.order.dto.Order
import cm.horion.homegaz.domain.model.order.dto.OrderState
import cm.horion.homegaz.presentation.ui.theme.homeGazColors
import cm.horion.homegaz.util.getDateOnly
import cm.horion.homegaz.util.getTimeOnly
import com.google.gson.Gson
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import io.github.alexzhirkevich.qrose.options.*

private fun Modifier.dashedBorder(
    color: Color,
    strokeWidth: Dp = 1.dp,
    cornerRadius: Dp = 16.dp,
    dashLength: Dp = 6.dp,
    gapLength: Dp = 4.dp,
): Modifier = this.drawWithContent {
    drawContent()
    val strokeWidthPx = strokeWidth.toPx()
    drawRoundRect(
        color = color,
        cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
        style = Stroke(
            width = strokeWidthPx,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(dashLength.toPx(), gapLength.toPx()), 0f
            )
        ),
        topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
        size = Size(
            size.width - strokeWidthPx,
            size.height - strokeWidthPx
        ),
    )
}

@Composable
fun ReservationDetailScreen(
    reservation: Order,
    gaz: GazBottle? = null,
    onBackClick: () -> Unit,
) {
    val colors = MaterialTheme.homeGazColors

    val (statusBg, statusContentColor, statusLabel) = when (reservation.orderState) {
        OrderState.STARTING -> Triple(colors.pendingBg, colors.pendingOnBg, stringResource(R.string.res_status_starting_label))
        OrderState.LOADING -> Triple(colors.deliveringBg, colors.deliveringOnBg, stringResource(R.string.res_status_loading_label))
        OrderState.SENDING -> Triple(colors.deliveringBg, colors.deliveringOnBg, stringResource(R.string.res_status_sending_label))
        OrderState.SHIPPING -> Triple(colors.deliveringBg, colors.deliveringOnBg, stringResource(R.string.res_status_shipping_label))
        OrderState.DELIVERED -> Triple(colors.deliveringBg, colors.deliveringOnBg, stringResource(R.string.res_status_delivered_label))
        OrderState.ENDING -> Triple(colors.completedBg, colors.completedOnBg, stringResource(R.string.res_status_completed_label))
        OrderState.CANCELLED -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, stringResource(R.string.res_status_cancelled_label))
    }

    val brandName = gaz?.company?.name ?: ""
    val quantity = reservation.gaz.getOrNull(0)?.quantity ?: 0
    val headerTitle = "$brandName-${gaz?.gazSize?.size ?: "12.5"}kg"

    val headerSubtitle = buildString {
        append(reservation.createdAt.getDateOnly())
        reservation.createdAt.getTimeOnly()?.let { append("  $it") }
    }
    val amountValue = reservation.amount ?: 0
    val phoneNumber = reservation.userUuid

    val appLogoPainter = painterResource(id = R.drawable.ic_launcher_playstore)

    //  CONVERSION DE LA DATA CLASS EN STRING JSON
    val orderJsonData = Gson().toJson(reservation)

    // CRÉATION DU PAINTER DE QR CODE AVEC LA STRING OBTENUE
    val qrCodePainter = rememberQrCodePainter(data = orderJsonData) {
        logo {
            painter = appLogoPainter
            size = 0.2f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header / TopBar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .height(56.dp),
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = headerTitle,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.primary,
                )
                if (headerSubtitle.isNotBlank()) {
                    Text(
                        text = headerSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // Zone centrale défilante (Ticket + Code QR)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .dashedBorder(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 1.dp,
                        cornerRadius = 16.dp,
                    )
                    .background(MaterialTheme.colorScheme.background),
            ) {
                DetailRow(
                    icon = Icons.Outlined.Verified,
                    label = stringResource(R.string.res_detail_label_marque),
                    value = brandName,
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.6.dp)

                DetailRow(
                    icon = Icons.Outlined.Scale,
                    label = stringResource(R.string.res_detail_label_poids),
                    value = "${gaz?.gazSize?.size ?: "12.5"}kg",
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.6.dp)

                DetailRow(
                    icon = Icons.Outlined.Numbers,
                    label = stringResource(R.string.label_quantity),
                    value = quantity.toString()
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.6.dp)

                DetailRow(
                    icon = Icons.Outlined.Payments,
                    label = stringResource(R.string.total_amount),
                    value = "$amountValue FCFA"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp).size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = qrCodePainter,
                        contentDescription = stringResource(R.string.res_detail_desc_qrcode),
                        modifier = Modifier.fillMaxSize()
                    )

            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.res_detail_instructions_qr),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

        @Composable
private fun DetailRow(
            icon: ImageVector,
            label: String,
            value: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                ),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}