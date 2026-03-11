package cm.horion.homegaz.presentation.ui.components.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.utils.ThemeColor

@Composable
fun HomeGazButton(
    text           : String,
    onClick        : () -> Unit,
    modifier       : Modifier = Modifier,
    icon           : ImageVector? = null,
    iconBeforeText : Boolean = false,
    containerColor : Color = ThemeColor.Primary,
    contentColor   : Color = Color.White,
    enabled        : Boolean = true,
    isOutlined     : Boolean = false
) {
    val resolvedContainer = when {
        isOutlined -> Color.Transparent
        else       -> containerColor
    }
    val resolvedContent = when {
        isOutlined -> if (contentColor == Color.White) ThemeColor.Primary else contentColor
        else       -> contentColor
    }
    val borderColor = when {
        isOutlined -> if (contentColor == Color.White) ThemeColor.Primary
        else contentColor
        else       -> Color.Transparent
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .then(
                if (isOutlined) Modifier.border(1.dp, borderColor, RoundedCornerShape(50.dp))
                else Modifier
            ),
        enabled = enabled,
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = resolvedContainer,
            contentColor   = resolvedContent
        ),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null && iconBeforeText) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text       = text,
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (icon != null && !iconBeforeText) {
                Spacer(Modifier.width(8.dp))
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
    }
}