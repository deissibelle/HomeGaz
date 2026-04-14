package cm.horion.homegaz.presentation.ui.pages.reservations

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import cm.horion.homegaz.domain.model.auth.AuthContext
import cm.horion.homegaz.presentation.ui.pages.auth.AuthGuardScreen
import cm.horion.homegaz.presentation.ui.components.reservations.*
import cm.horion.homegaz.presentation.viewmodel.ReservationsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationsScreen(
    navController: NavController,
    viewModel: ReservationsViewModel = koinViewModel()
) {
    val isLoggedIn by remember { mutableStateOf(true) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // État local de la barre de recherche
    var searchQuery by remember { mutableStateOf("") }

    // Filtrage des réservations selon la recherche
    val filteredReservations = remember(uiState.reservations, searchQuery) {
        if (searchQuery.isBlank()) uiState.reservations
        else uiState.reservations.filter { res ->
            res.id.contains(searchQuery, ignoreCase = true) ||
                    res.brand.contains(searchQuery, ignoreCase = true) ||
                    res.weight.contains(searchQuery, ignoreCase = true) ||
                    res.status.label.contains(searchQuery, ignoreCase = true)
        }
    }

    if (!isLoggedIn) {
        AuthGuardScreen(
            authContext = AuthContext(
                title = "Mes Réservations",
                description = "Connecte-toi pour consulter l'historique de tes commandes de gaz.",
                icon = Icons.Default.ReceiptLong
            ),
            onLoginClick = {},
            onRegisterClick = {},
            onForgotPasswordClick = {}
        )
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mes Réservations",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            StatusHeader(
                active = uiState.activeCount,
                completed = uiState.completedCount
            )

            Spacer(Modifier.height(8.dp))

            ReservationSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            Spacer(Modifier.height(12.dp))

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                uiState.error != null -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(
                            "Une erreur est survenue : ${uiState.error}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                filteredReservations.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(
                            if (searchQuery.isBlank()) "Aucune réservation pour l'instant."
                            else "Aucun résultat pour \"$searchQuery\".",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = filteredReservations,
                            key = { it.id }
                        ) { reservation ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(tween(300)) + slideInVertically(
                                    initialOffsetY = { it / 4 },
                                    animationSpec = tween(300)
                                )
                            ) {
                                ReservationItem(res = reservation)
                            }
                        }
                        item { Spacer(Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}