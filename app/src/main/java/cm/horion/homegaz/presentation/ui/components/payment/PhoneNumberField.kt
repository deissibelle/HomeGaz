package cm.horion.homegaz.presentation.ui.components.payment


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.presentation.ui.theme.bodyFontFamily


@Composable
fun PhoneNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Text(
            text = "Numéro",
            style = TextStyle(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = "Ex: 673219684",
                    style = TextStyle(
                        fontFamily = bodyFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            textStyle = TextStyle(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}