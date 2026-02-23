package cm.horion.homegaz.presentation.ui.location


import android.Manifest
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.presentation.ui.theme.HomeGazTheme
import cm.horion.homegaz.utils.ThemeColor
import cm.horion.homegaz.utils.ThemeColor.IconBackgroundColor
import cm.horion.homegaz.utils.ThemeColor.SubtitleColor
import com.google.accompanist.permissions.*



@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionScreen(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit = {}
) {
    val locationPermission = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    ) { granted ->
        if (granted) onPermissionGranted() else onPermissionDenied()
    }
    LocationPermissionContent(
        onActivateClick = { locationPermission.launchPermissionRequest() }
    )
}

@Composable
internal fun LocationPermissionContent(
    onActivateClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(IconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.sad),
                    contentDescription = "Localisation désactivée",
                    modifier = Modifier.size(52.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Activez\nla localisation et\ndécouvrez le point de gaz\nle plus proche de vous",
                color = ThemeColor.Primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = (24 * 1.17).sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Votre position est utilisée\nuniquement pour vous montrer\nles points de gaz les plus proches.",
                color = SubtitleColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = (15 * 1.20).sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.85f)
            )
            Spacer(modifier = Modifier.height(80.dp))
            Button(
                onClick = onActivateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ThemeColor.Primary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Text(
                    text = "J'active",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LocationPermissionPreview() {
    HomeGazTheme {
        LocationPermissionContent()
    }
}