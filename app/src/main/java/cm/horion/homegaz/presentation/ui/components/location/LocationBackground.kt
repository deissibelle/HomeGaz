package cm.horion.homegaz.presentation.ui.components.location

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.R
import cm.horion.homegaz.utils.ThemeColor

@Composable
fun LocationBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Icon(
            painter = painterResource(R.drawable.marker),
            contentDescription = null,
            tint = ThemeColor.Primary.copy(alpha = 0.05f),
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-20).dp)
        )
        Icon(
            painter = painterResource(R.drawable.marker),
            contentDescription = null,
            tint = ThemeColor.Primary.copy(alpha = 0.05f),
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 40.dp)
        )
    }
}
