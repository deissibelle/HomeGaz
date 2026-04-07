package cm.horion.homegaz.presentation.ui.components.gazprofile

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.R





@Composable
fun GazProfileLocationField(
    value         : String,
    onValueChange : (String) -> Unit,
    onDetectClick : () -> Unit = {},
    showLabel     : Boolean = true
) {
    GazProfileFieldRow(
        icon  = Icons.Outlined.LocationOn,
        label = if (showLabel) stringResource(R.string.gaz_profile_location) else ""
    ) {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            placeholder   = {
                Text(
                    stringResource(R.string.gaz_profile_location_hint),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine   = true,
            modifier     = Modifier.fillMaxWidth(),
            shape        = RoundedCornerShape(10.dp),
            trailingIcon = {
                IconButton(onClick = onDetectClick) {
                    Icon(
                        imageVector        = Icons.Outlined.MyLocation,
                        contentDescription = stringResource(R.string.gaz_profile_location_detect),
                        tint               = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = gazProfileFieldColors()
        )
    }
}