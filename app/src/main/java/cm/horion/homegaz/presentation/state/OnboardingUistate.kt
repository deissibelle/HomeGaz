package cm.horion.homegaz.presentation.state

import cm.horion.homegaz.domain.model.Onboarding

data class OnboardingUiState(
    val pages: List<Onboarding> = emptyList()
)