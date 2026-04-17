package cm.horion.homegaz.presentation.ui.components.reservations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R

@Composable
fun ReservationSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value         = query,
        onValueChange = onQueryChange,
        modifier      = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        placeholder = {
            Text(
                text  = stringResource(R.string.res_search_placeholder),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
            )
        },
        leadingIcon = {
            Icon(
                imageVector        = Icons.Default.Search,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(18.dp),
            )
        },
        singleLine  = true,
        shape       = RoundedCornerShape(12.dp),
        colors      = OutlinedTextFieldDefaults.colors(
            focusedBorderColor    = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor  = MaterialTheme.colorScheme.outlineVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor      = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor    = MaterialTheme.colorScheme.onSurface,
            cursorColor           = MaterialTheme.colorScheme.primary,
        ),
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
    )
}