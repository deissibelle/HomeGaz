package cm.horion.homegaz.presentation.ui.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.presentation.ui.components.common.CustomDropdown
import cm.horion.homegaz.presentation.ui.components.common.HomeGazButton


@Composable
fun HomeFilterCard(
    distributor: String,
    onDistributorChange: (String) -> Unit,
    distance: String,
    onDistanceChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit,
    onRefresh: () -> Unit,
    distributorOptions: List<String>,
    distanceOptions: List<String>,
    weightOptions: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                CustomDropdown(
                    selected = distributor,
                    options = distributorOptions,
                    modifier = Modifier.weight(1f),
                    onSelect = onDistributorChange
                )
                Spacer(Modifier.width(8.dp))
                CustomDropdown(
                    selected = distance,
                    options = distanceOptions,
                    modifier = Modifier.weight(1f),
                    onSelect = onDistanceChange
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomDropdown(
                    selected = weight,
                    options = weightOptions,
                    modifier = Modifier.weight(1f),
                    onSelect = onWeightChange
                )
                Spacer(Modifier.width(12.dp))
                HomeGazButton(
                    text = "Actualiser",
                    onClick = onRefresh,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}