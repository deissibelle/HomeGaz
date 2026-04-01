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
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding()
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
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PagerIndicator(
                    size = pages.size,
                    currentPage = pagerState.currentPage
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
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

        Image(
            painter = painterResource(id = page.image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

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
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}