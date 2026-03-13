package cm.horion.homegaz.presentation.ui.components.home


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.R

@Composable
fun RecenterButton(
    onClick : () -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        painter            = painterResource(R.drawable.foundation_target_two),
        contentDescription = "Recentrer sur ma position",
        modifier           = modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable { onClick() }
    )
}