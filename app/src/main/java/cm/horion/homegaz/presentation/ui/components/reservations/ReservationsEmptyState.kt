package cm.horion.homegaz.presentation.ui.components.reservations

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPasteSearch
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R


@Composable
fun ReservationEmptyState(
    onReserveClick: () -> Unit = {},
) {
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier            = Modifier
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector        = Icons.Outlined.ContentPasteSearch,
                contentDescription = stringResource(R.string.res_empty_icon_desc),
                modifier           = Modifier.size(72.dp),
                tint               = MaterialTheme.colorScheme.primary,
            )

            Text(
                text       = stringResource(R.string.res_empty_title),
                style      = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp,
                ),
                color     = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )

            Text(
                text      = stringResource(R.string.res_empty_desc),
                style     = MaterialTheme.typography.bodyMedium,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            TextButton(onClick = onReserveClick) {
                Text(
                    text      = stringResource(R.string.res_empty_cta),
                    style     = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}