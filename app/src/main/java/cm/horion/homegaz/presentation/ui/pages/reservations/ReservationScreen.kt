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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.auth.AuthContext
import cm.horion.homegaz.presentation.ui.pages.auth.AuthGuardScreen
import cm.horion.homegaz.presentation.ui.components.reservations.*
import cm.horion.homegaz.presentation.viewmodel.ReservationsViewModel
import cm.horion.homegaz.presentation.ui.theme.HG_Background_Light
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationsScreen(
    navController: NavController,
    viewModel: ReservationsViewModel = koinViewModel()
) {
    val isLoggedIn by remember { mutableStateOf(true) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    val filteredReservations = remember(uiState.reservations, searchQuery) {
        if (searchQuery.isBlank()) uiState.reservations
        else uiState.reservations.filter { res ->
            res.id.contains(searchQuery, ignoreCase = true) ||
                    res.brand.contains(searchQuery, ignoreCase = true)
        }
    }

    if (!isLoggedIn) {
        AuthGuardScreen(
            authContext = AuthContext(
                title = stringResource(R.string.auth_title_reservations),
                description = stringResource(R.string.auth_desc_reservations),
                icon = Icons.Default.ReceiptLong
            ),
            onLoginClick = { },
            onRegisterClick = { },
            onForgotPasswordClick = { }
        )
        return
    }

    Scaffold(
        containerColor = HG_Background_Light,
        topBar = {
            Column(
                modifier = Modifier
                    .background(HG_Background_Light)
                    .padding(horizontal = 8.dp)

                ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = null )

                    }
                    Text(
                        text = stringResource(R.string.res_screen_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                StatusHeader(
                    active = uiState.activeCount,
                    completed = uiState.completedCount
                )

                Spacer(Modifier.height(4.dp))

                ReservationSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )

                Spacer(Modifier.height(8.dp))
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
        ) {
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                uiState.error != null -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(
                            text = uiState.error ?: "Erreur inconnue",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                filteredReservations.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(
                            text = if (searchQuery.isBlank()) "Aucune réservation trouvée"
                            else "Aucun résultat pour \"$searchQuery\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = filteredReservations,
                            key = { it.id }
                        ) { reservation ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(tween(300)) + slideInVertically(
                                    initialOffsetY = { it / 4 }
                                )
                            ) {
                                ReservationItem(
                                    res = reservation,
                                    onClick = { }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
