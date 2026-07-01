package cm.horion.homegaz.presentation.ui.pages.distributor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle
import cm.horion.homegaz.domain.model.distributor.DeliveryOption
import cm.horion.homegaz.domain.model.distributor.dto.Distributor
import cm.horion.homegaz.domain.model.home.DistributorPoint
import cm.horion.homegaz.presentation.state.DistributorDetailUiState
import cm.horion.homegaz.presentation.ui.components.common.HomeGazButton
import cm.horion.homegaz.presentation.ui.components.common.WarningNote
import cm.horion.homegaz.presentation.ui.components.distributor.DeliveryOptionRow
import cm.horion.homegaz.presentation.ui.components.distributor.DistributorHeader
import cm.horion.homegaz.presentation.ui.components.distributor.ProductInfoRow
import cm.horion.homegaz.presentation.ui.components.distributor.QuantitySelector
import cm.horion.homegaz.presentation.ui.components.distributor.TotalAmountCard
import cm.horion.homegaz.presentation.viewmodel.DistributorDetailViewModel
import org.koin.androidx.compose.koinViewModel



@Composable
fun DistributorPointDetailScreen(
    point      : Distributor,
    battleUuid : String = "",
    onBackClick: () -> Unit,
    onNextClick: (quantity: Int, option: DeliveryOption) -> Unit,
    viewModel  : DistributorDetailViewModel = koinViewModel()
) {
    LaunchedEffect(battleUuid, point.enterpriseUuid) {
        viewModel.cleanPayment()
        viewModel.loadPoint(point)
        viewModel.loadAvailableBottles(
            battleUuid = battleUuid,
            stock = point.stock ?: emptyMap()
        )
    }

    val uiState by viewModel.uiState.collectAsState()

    DistributorDetailContent(
        uiState                = uiState,
        battleUuid             = battleUuid,
        product                = point,
        onBackClick            = onBackClick,
        onNextClick            = onNextClick,
        onQuantityChange       = viewModel::onQuantityChange,
        onDeliveryOptionChange = viewModel::onDeliveryOptionChange,
        onBottleSelected       = viewModel::onBottleSelected
    )
}


@Composable
private fun DistributorDetailContent(
    uiState                : DistributorDetailUiState,
    battleUuid             : String,
    product                : Distributor,
    onBackClick            : () -> Unit,
    onNextClick            : (quantity: Int, option: DeliveryOption) -> Unit,
    onQuantityChange       : (Int) -> Unit,
    onDeliveryOptionChange : (DeliveryOption) -> Unit,
    onBottleSelected       : (GazBottle) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Le Header reste toujours visible pour permettre le retour en arrière
        DistributorHeader(
            title       = product.name,
            logoRes     = null,
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── 🛡️ SÉCURITÉ : GESTION DU CAS "AUCUN GAZ DISPONIBLE" ──
        if (!uiState.isLoading && uiState.availableBottles.isEmpty() && uiState.gaz == null) {
            Box(
                modifier = Modifier
                    //.fill someMaxSize()
                    .weight(1f)
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info, // Tu peux mettre un icône de bouteille barrée si tu as
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Aucun gaz disponible",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ce dépôt n'a actuellement aucune bouteille en stock correspondant à votre recherche.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            return@Column // On s'arrête ici, le reste du formulaire ne s'affiche pas
        }

        // ── FLUX NORMAL (S'affiche uniquement si du gaz est présent) ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            if (battleUuid.isEmpty()) {
                Text(
                    text = "Sélectionnez votre format de bouteille :",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.availableBottles) { bottle ->
                        val isSelected = bottle.uuid == uiState.gaz?.uuid

                        FilterChip(
                            selected = isSelected,
                            onClick = { onBottleSelected(bottle) },
                            label = {
                                Text(text = "${bottle.company.name} (${bottle.gazSize.size} kg)")
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            ProductInfoRow(
                label = "Marque sélectionnée",
                value = uiState.gaz?.company?.name ?: "Aucun gaz trouvé",
                icon = Icons.Outlined.Settings
            )

            ProductInfoRow(
                label = "Type de Gaz / Poids",
                value = uiState.gaz?.let { "${it.gazType} - ${it.gazSize.size} kg" } ?: "--",
                icon = Icons.Outlined.Scale
            )

            QuantitySelector(
                quantity         = uiState.quantity,
                onQuantityChange = onQuantityChange
            )

            DeliveryOptionRow(
                selectedOption   = uiState.selectedOption,
                onOptionSelected = onDeliveryOptionChange
            )

            Spacer(modifier = Modifier.height(20.dp))

            TotalAmountCard(total = uiState.total)

            Spacer(modifier = Modifier.height(12.dp))

            WarningNote(
                message = stringResource(R.string.delivery_warning_note)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Le bouton s'affiche uniquement si la validation de commande est possible
            if (uiState.gaz != null) {
                HomeGazButton(
                    text     = stringResource(R.string.next),
                    onClick  = { onNextClick(uiState.quantity, uiState.selectedOption) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 32.dp)
                )
            }
        }
    }
}