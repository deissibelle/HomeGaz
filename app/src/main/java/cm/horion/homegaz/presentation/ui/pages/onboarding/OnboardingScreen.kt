@file:OptIn(ExperimentalFoundationApi::class)

package cm.horion.homegaz.presentation.ui.pages.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.onboarding.Onboarding
import cm.horion.homegaz.presentation.ui.components.common.HomeGazButton
import cm.horion.homegaz.presentation.ui.components.onboarding.PagerIndicator
import kotlinx.coroutines.launch
import cm.horion.homegaz.presentation.viewmodel.OnboardingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val onboardingPages = listOf(
        Onboarding(
            title = stringResource(R.string.onboarding_title_1),
            description = stringResource(R.string.onboarding_desc_1),
            image = R.drawable.map
        ),
        Onboarding(
            title = stringResource(R.string.onboarding_title_3),
            description = stringResource(R.string.onboarding_desc_3),
            image = R.drawable.pay
        )
    )

    LaunchedEffect(Unit) {
        viewModel.setPages(onboardingPages)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pages = uiState.pages

    if (pages.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex

    Scaffold(
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(onClick = { viewModel.finishOnboarding(onFinish) }) {
                    Text(
                        text = stringResource(R.string.skip),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top
            ) { index ->
                OnboardingPage(page = pages[index])
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PagerIndicator(
                    size = pages.size,
                    currentPage = pagerState.currentPage
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pagerState.currentPage > 0) {
                        HomeGazButton(
                            iconBeforeText = true,
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            text = stringResource(R.string.back),
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            isOutlined = true,
                            modifier = Modifier.wrapContentWidth()
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    HomeGazButton(
                        text = if (isLastPage)
                            stringResource(R.string.onboarding_finish)
                        else
                            stringResource(R.string.next),
                        icon = if (!isLastPage)
                            Icons.AutoMirrored.Filled.ArrowForward
                        else null,
                        onClick = {
                            if (isLastPage) {
                                viewModel.finishOnboarding(onFinish)
                            } else {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        modifier = Modifier.wrapContentWidth()
                    )
                }
            }
        }
    }
}
@Composable
fun OnboardingPage(page: Onboarding) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth(0.7f)
                .heightIn(min = 60.dp, max = 90.dp),
            contentScale = ContentScale.Fit
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(1f)
                .aspectRatio(1f)
        ) {
            Image(
                painter = painterResource(id = page.image),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            if (page.image == R.drawable.map) {

                MapLabelRaw(
                    text = "Algo gaz",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 75.dp, top = 45.dp)
                )

                MapLabelRaw(
                    text = "Globus gaz",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 40.dp, start = 10.dp)
                )

                MapLabelRaw(
                    text = "Comex",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 55.dp, top = 30.dp)
                )

                MapLabelRaw(
                    text = "InterCom",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(end = 20.dp, bottom = 40.dp)
                )
                MapLabelRaw(
                    text = "Total",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 30.dp, bottom = 90.dp)
                )

                MapLabelRaw(
                    text = "Moi",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 70.dp, start = 40.dp),
                    isUser = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun MapLabelRaw(
    text: String,
    modifier: Modifier = Modifier,
    isUser: Boolean = false
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall.copy(
            shadow = Shadow(
                color = Color.White.copy(alpha = 0.8f),
                offset = Offset(2f, 2f),
                blurRadius = 4f
            )
        ),
        fontWeight = FontWeight.ExtraBold,
        color = if (isUser) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
    )
}