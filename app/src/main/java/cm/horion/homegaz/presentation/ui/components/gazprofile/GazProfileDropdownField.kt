package cm.horion.homegaz.presentation.ui.components.gazprofile


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GazProfileDropdownField(
    icon     : ImageVector,
    label    : String,
    options  : List<String>,
    selected : String,
    onSelect : (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    GazProfileFieldRow(icon = icon, label = label) {
        ExposedDropdownMenuBox(
            expanded         = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value         = selected,
                onValueChange = {},
                readOnly      = true,
                placeholder   = {
                    Text(
                        "Sélectionner",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier     = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape  = RoundedCornerShape(10.dp),
                colors = gazProfileFieldColors()
            )

            ExposedDropdownMenu(
                expanded         = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text    = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}