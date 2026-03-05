package cm.horion.homegaz.presentation.state

import cm.horion.homegaz.domain.model.home.DistributionPoint

data class HomeUiState(
    val allPoints: List<DistributionPoint> = emptyList(),
    val filteredPoints: List<DistributionPoint> = emptyList(),
    val selectedPoint: DistributionPoint? = null,

    val selectedDistributor: String = "SCTM",
    val selectedDistance: String = "5 km",
    val selectedWeight: String = "12kg",

    val locationGranted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)