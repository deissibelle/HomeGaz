package cm.horion.homegaz.presentation.ui.components.home


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.UiMarker

@Composable
fun InteractiveMap(
    markers: List<UiMarker>,
    onMarkerClick: (UiMarker) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        Image(
            painter = painterResource(R.drawable.map_vide),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        markers.forEach { marker ->
            Image(
                painter = painterResource(R.drawable.marker),
                contentDescription = null,
                modifier = Modifier
                    .offset(
                        x = screenWidth * (marker.x / 393f),
                        y = screenHeight * (marker.y / 852f)
                    )
                    .size(
                        screenWidth * (marker.width / 393f),
                        screenHeight * (marker.height / 852f)
                    )
                    .clickable { onMarkerClick(marker) }
            )
        }

        Box(modifier = Modifier.align(Alignment.Center)) {
            UserMarker()
        }
    }
}
