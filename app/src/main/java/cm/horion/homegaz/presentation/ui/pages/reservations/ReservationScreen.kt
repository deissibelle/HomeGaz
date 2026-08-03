package cm.horion.homegaz.presentation.ui.pages.reservations

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.order.dto.Order
import cm.horion.homegaz.presentation.state.ReservationsUiState
import cm.horion.homegaz.presentation.ui.components.reservations.ReservationEmptyState
import cm.horion.homegaz.presentation.ui.components.reservations.ReservationListItem
import cm.horion.homegaz.presentation.viewmodel.ReservationsViewModel


@Composable
fun ReservationsScreen(
    viewModel     : ReservationsViewModel,
    onNavigateToHomeTab: () -> Unit

) {
    LaunchedEffect(Unit) {
        viewModel.loadReservations()
    }

    val uiState     by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    var selectedReservation by remember { mutableStateOf<Order?>(null) }

    BackHandler(enabled = selectedReservation != null) {
        selectedReservation = null
    }

    val filteredReservations = remember(uiState.orders, searchQuery) {
        if (searchQuery.isBlank()) {
            uiState.orders
        } else {
            uiState.orders.filter { res ->
                res.uuid.contains(searchQuery, ignoreCase = true)

            }
        }
    }

    AnimatedContent(
        targetState   = selectedReservation,
        transitionSpec = {
            if (targetState != null) {
                // Entrée détail : slide depuis la droite
                (slideInHorizontally(tween(300)) { it } + fadeIn(tween(300))) togetherWith
                        (slideOutHorizontally(tween(250)) { -it / 3 } + fadeOut(tween(150)))
            } else {
                // Retour liste : slide vers la droite
                (slideInHorizontally(tween(300)) { -it / 3 } + fadeIn(tween(300))) togetherWith
                        (slideOutHorizontally(tween(250)) { it } + fadeOut(tween(150)))
            }
        },
        label = "res_list_detail_nav",
    ) { selected ->
        if (selected != null) {
            viewModel.detailGaz(selected.gaz[0].bottleUuid)
            ReservationDetailScreen(
                reservation = selected,
                gaz = uiState.gaz,
                onBackClick = { selectedReservation = null },
            )
        } else {
            ReservationListContent(
                viewModel        = viewModel,
                uiState              = uiState,
                searchQuery          = searchQuery,
                onSearchQueryChange  = { searchQuery = it },
                filteredReservations = filteredReservations,
                onReservationClick   = { selectedReservation = it },
                onNavigateToHomeTab  = onNavigateToHomeTab
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReservationListContent(
    viewModel     : ReservationsViewModel,
    uiState              : ReservationsUiState,
    searchQuery          : String,
    onSearchQueryChange  : (String) -> Unit,
    filteredReservations : List<Order>,
    onReservationClick   : (Order) -> Unit,
    onNavigateToHomeTab: () -> Unit

) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
            ) {

                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {

                    Text(
                        text  = stringResource(R.string.res_screen_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize   = 20.sp,
                        ),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding()),
        ) {
            when {
                // Chargement
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color    = MaterialTheme.colorScheme.primary,
                    )
                }

                // Erreur
                uiState.error != null -> {
                    Text(
                        text     = uiState.error
                            ?: stringResource(R.string.res_error_unknown),
                        modifier = Modifier.align(Alignment.Center),
                        style    = MaterialTheme.typography.bodyMedium,
                        color    = MaterialTheme.colorScheme.error,
                    )
                }

                // Aucune réservation → état vide
                uiState.orders.isEmpty() -> {
                    ReservationEmptyState(
                        onReserveClick = onNavigateToHomeTab
                    )
                }

                // Recherche sans résultat
                filteredReservations.isEmpty() -> {
                    Text(
                        text     = stringResource(R.string.res_search_no_result, searchQuery),
                        modifier = Modifier.align(Alignment.Center),
                        style    = MaterialTheme.typography.bodyMedium,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Liste des réservations
                else -> {
                    LazyColumn(
                        modifier            = Modifier.fillMaxSize(),
                        contentPadding      = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                    ) {
                        items(
                            items = filteredReservations,
                            key   = { it.uuid },
                        ) { reservation ->
                            val company = viewModel.getCompany(reservation.gaz[0].bottleUuid)
                            val weight  = viewModel.getWeight(reservation.gaz[0].bottleUuid)
                            AnimatedVisibility(
                                visible = true,
                                enter   = fadeIn(tween(300)) + slideInVertically(
                                    animationSpec  = tween(300),
                                    initialOffsetY = { it / 4 },
                                ),
                            ) {
                                ReservationListItem(
                                    res     = reservation,
                                    company = company,
                                    weight  = weight,
                                    onClick = { onReservationClick(reservation) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}