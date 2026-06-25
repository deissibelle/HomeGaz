package cm.horion.homegaz.presentation.ui.pages.reservations

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.auth.AuthContext
import cm.horion.homegaz.domain.model.reservation.Reservation
import cm.horion.homegaz.presentation.state.ReservationsUiState
import cm.horion.homegaz.presentation.ui.components.reservations.ReservationEmptyState
import cm.horion.homegaz.presentation.ui.components.reservations.ReservationListItem
import cm.horion.homegaz.presentation.ui.components.reservations.ReservationSearchBar
import cm.horion.homegaz.presentation.ui.components.reservations.StatusHeader
import cm.horion.homegaz.presentation.ui.pages.auth.AuthGuardScreen
import cm.horion.homegaz.presentation.ui.theme.HG_Background_Light
import cm.horion.homegaz.presentation.viewmodel.ReservationsViewModel


@Composable
fun ReservationsScreen(
    navController : NavController,
    viewModel     : ReservationsViewModel,
) {
    val isLoggedIn by remember { mutableStateOf(false) }

    val uiState     by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    // Réservation sélectionnée
    var selectedReservation by remember { mutableStateOf<Reservation?>(null) }

    val filteredReservations = remember(uiState.reservations, searchQuery) {
        if (searchQuery.isBlank()) {
            uiState.reservations
        } else {
            uiState.reservations.filter { res ->
                res.id.contains(searchQuery, ignoreCase = true) ||
                        res.brand.contains(searchQuery, ignoreCase = true)
            }
        }
    }

//    if (!isLoggedIn) {
//        AuthGuardScreen(
//            authContext = AuthContext(
//                title       = stringResource(R.string.auth_title_reservations),
//                description = stringResource(R.string.auth_desc_reservations),
//                icon        = Icons.Default.ReceiptLong,
//            ),
//            onLoginClick          = {},
//            onRegisterClick       = {},
//            onForgotPasswordClick = {},
//        )
//        return
//    }

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
            ReservationDetailScreen(
                reservation = selected,
                onBackClick = { selectedReservation = null },
            )
        } else {
            ReservationListContent(
                navController        = navController,
                uiState              = uiState,
                searchQuery          = searchQuery,
                onSearchQueryChange  = { searchQuery = it },
                filteredReservations = filteredReservations,
                onReservationClick   = { selectedReservation = it },
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReservationListContent(
    navController        : NavController,
    uiState              : ReservationsUiState,
    searchQuery          : String,
    onSearchQueryChange  : (String) -> Unit,
    filteredReservations : List<Reservation>,
    onReservationClick   : (Reservation) -> Unit,
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

                // StatusHeader + SearchBar
                if (uiState.reservations.isNotEmpty()) {
                    StatusHeader(
                        active    = uiState.activeCount,
                        completed = uiState.completedCount,
                    )
                    Spacer(Modifier.height(4.dp))
                    ReservationSearchBar(
                        query         = searchQuery,
                        onQueryChange = onSearchQueryChange,
                    )
                    Spacer(Modifier.height(8.dp))
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
                uiState.reservations.isEmpty() -> {
                    ReservationEmptyState()
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
                            key   = { "${it.id}_${it.date}_${it.time}" },
                        ) { reservation ->
                            AnimatedVisibility(
                                visible = true,
                                enter   = fadeIn(tween(300)) + slideInVertically(
                                    animationSpec  = tween(300),
                                    initialOffsetY = { it / 4 },
                                ),
                            ) {
                                ReservationListItem(
                                    res     = reservation,
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