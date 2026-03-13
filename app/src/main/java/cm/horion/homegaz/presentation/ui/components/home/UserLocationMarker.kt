package cm.horion.homegaz.presentation.ui.components.home


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.R
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun UserLocationMarker(
    photoUrl: String? = null
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Box(
                modifier         = Modifier
                    .size(52.dp)
                    .border(2.5.dp, MaterialTheme.colorScheme.tertiary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Ma position",
                    contentScale       = ContentScale.Crop,
                    placeholder        = painterResource(R.drawable.profil),
                    error              = painterResource(R.drawable.profil),
                    fallback           = painterResource(R.drawable.profil),
                    modifier           = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text  = "Moi",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}