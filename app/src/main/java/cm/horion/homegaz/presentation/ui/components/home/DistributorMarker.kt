package cm.horion.homegaz.presentation.ui.components.home


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.R

@Composable
fun DistributorMarker(
    name      : String,
    isSelected: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        if (name.isNotBlank()) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text  = name,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.7f,
                        color    = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
        }

        Image(
            painter            = painterResource(R.drawable.marker),
            contentDescription = name,
            contentScale       = ContentScale.Fit,
            modifier           = Modifier.size(
                width  = if (isSelected) 48.dp else 40.dp,
                height = if (isSelected) 60.dp else 50.dp
            )
        )
    }
}