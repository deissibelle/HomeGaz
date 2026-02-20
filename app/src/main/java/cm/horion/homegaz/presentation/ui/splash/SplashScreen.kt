package cm.horion.homegaz.presentation.ui.splash


import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import cm.horion.homegaz.R
import cm.horion.homegaz.utils.ThemeColor


@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit = {},
) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ThemeColor.Primary),
        contentAlignment = Alignment.Center
    ) {
        LogoSection(startAnimation = startAnimation)
    }
}

@Composable
fun LogoSection(startAnimation: Boolean) {
    // Animation de scale pour le logo
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_scale"
    )

    // Animation de fade pour le logo
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "logo_alpha"
    )

    Image(
        painter = painterResource(id = R.drawable.logoblanc),
        contentDescription = "logo",
        modifier = Modifier
            .width(203.dp)
            .height(220.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                alpha = alpha
            ),
        contentScale = ContentScale.Fit
    )
}

@Preview
@Composable
fun SplashPreview() {
    SplashScreen()
}