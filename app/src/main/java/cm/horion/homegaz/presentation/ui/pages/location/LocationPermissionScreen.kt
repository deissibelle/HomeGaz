package cm.horion.homegaz.presentation.ui.pages.location

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.presentation.ui.components.common.HomeGazButton
import cm.horion.homegaz.presentation.ui.components.location.LocationBackground
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionScreen(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {

    var permissionRequested by remember { mutableStateOf(false) }

    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    ) { isGranted ->
        if (permissionRequested) {
            if (isGranted) onPermissionGranted() else onPermissionDenied()
        }
    }

    LocationPermissionContent(
        onActivateClick = {
            permissionRequested = true
            locationPermissionState.launchPermissionRequest()
        }
    )
}

@Composable
internal fun LocationPermissionContent(onActivateClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LocationBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.sad),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.location_title),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.location_subtitle),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(60.dp))

            HomeGazButton(
                text = stringResource(R.string.location_btn_activate),
                onClick = onActivateClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}