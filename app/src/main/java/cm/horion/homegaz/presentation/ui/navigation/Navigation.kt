package cm.horion.homegaz.presentation.ui.navigation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import cm.horion.homegaz.domain.model.common.Destination
import cm.horion.homegaz.domain.model.distributor.PaymentMethod
import cm.horion.homegaz.domain.model.payment.dto.PaymentStatus
import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.presentation.ui.MainScreen
import cm.horion.homegaz.presentation.ui.Tab
import cm.horion.homegaz.presentation.ui.pages.account.HelpCenterScreen
import cm.horion.homegaz.presentation.ui.pages.account.PrivacySettingsScreen
import cm.horion.homegaz.presentation.ui.pages.advices.AdvicesScreen
import cm.horion.homegaz.presentation.ui.pages.confirmation.ConfirmationScreen
import cm.horion.homegaz.presentation.ui.pages.distributor.DistributorPointDetailScreen
import cm.horion.homegaz.presentation.ui.pages.gazprofile.GazProfileScreen
import cm.horion.homegaz.presentation.ui.pages.location.LocationPermissionScreen
import cm.horion.homegaz.presentation.ui.pages.onboarding.OnboardingScreen
import cm.horion.homegaz.presentation.ui.pages.payment.PaymentInitiatedScreen
import cm.horion.homegaz.presentation.ui.pages.payment.PaymentScreen
import cm.horion.homegaz.presentation.ui.pages.payment.PaymentSuccessScreen
import cm.horion.homegaz.presentation.ui.theme.HomeGazTheme
import cm.horion.homegaz.presentation.viewmodel.ConsumerViewModel
import cm.horion.homegaz.presentation.viewmodel.DistributorDetailViewModel
import cm.horion.homegaz.presentation.viewmodel.HomeViewModel
import cm.horion.homegaz.presentation.viewmodel.ReservationsViewModel
import org.koin.androidx.compose.koinViewModel


private fun NavBackStack<NavKey>.popUntil(predicate: (NavKey) -> Boolean) {
    while (size > 1 && !predicate(last())) {
        removeLastOrNull()
    }
}

@Composable
fun HomeGazApp(
    userPrefs: UserPreferencesRepository,
    isLoggedIn      : Boolean,
    onSsoLoginCall  : () -> Unit,
    onSsoLogoutCall : () -> Unit
) = HomeGazTheme {

    val onboardingCompleted by userPrefs.isOnboardingCompleted.collectAsState(initial = null)
    if (onboardingCompleted == null) return@HomeGazTheme

    val backStack = rememberNavBackStack(
        if (onboardingCompleted == true) Destination.Home else Destination.Onboarding
    )

    var pendingHomeTab by remember { mutableStateOf<Tab?>(null) }

    val homeViewModel: HomeViewModel = koinViewModel()
    val consumerViewModel: ConsumerViewModel = koinViewModel()
    val distributorViewModel: DistributorDetailViewModel = koinViewModel()
    val reservationsViewModel: ReservationsViewModel = koinViewModel()
    val sharedUiState by distributorViewModel.uiState.collectAsState()

    NavDisplay(
        backStack = backStack,
        onBack    = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {

            // ── ONBOARDING ────────────────────────────────────────────────
            entry<Destination.Onboarding> {
                OnboardingScreen(
                    onFinish = {
                        backStack.clear()
                        backStack.add(Destination.Home)
                    }
                )
            }

            // ── LOCATION PERMISSION ──────────────────────────────────────
            entry<Destination.LocationPermission> {
                val locationPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val granted = permissions.values.all { it }
                    homeViewModel.onLocationPermissionResult(granted)
                    if (granted) backStack.removeLastOrNull()
                }
                LocationPermissionScreen(
                    onActivateClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                )
            }

            // ── HOME ──────────────────────────────────────────────────────
            entry<Destination.Home> {
                MainScreen(
                    backStack              = backStack,
                    reservationsViewModel  = reservationsViewModel,
                    consumerViewModel      = consumerViewModel,
                    onRouteClick           = { lat, lng -> homeViewModel.calculateRouteToPoint(lat, lng) },
                    onRequestLocation      = { backStack.add(Destination.LocationPermission) },
                    requestedTab           = pendingHomeTab,
                    onRequestedTabConsumed = { pendingHomeTab = null },
                    isLoggedIn             = isLoggedIn,
                    onSsoLoginCall         = onSsoLoginCall,
                    onSsoLogoutCall        = onSsoLogoutCall
                )
            }

            // ── ÉCRANS SECONDAIRES DE COMPTE ─────────────────────────────
            entry<Destination.HelpCenter> {
                HelpCenterScreen(onBackClick = { backStack.removeLastOrNull() })
            }

            entry<Destination.PrivacySettings> {
                PrivacySettingsScreen(onBackClick = { backStack.removeLastOrNull() })
            }

            entry<Destination.GazProfile> {
                GazProfileScreen(
                    onBackClick = { backStack.removeLastOrNull() },
                    onSaved     = { backStack.removeLastOrNull() }
                )
            }

            entry<Destination.Advices> {
                AdvicesScreen(onBackClick = { backStack.removeLastOrNull() })
            }

            // ── DISTRIBUTEUR DETAIL ──────────────────────────────────────
            entry<Destination.DistributorDetail> { key ->
                val uiState by consumerViewModel.uiState.collectAsState()
                val point = uiState.allPoints.find { it.enterpriseUuid == key.pointId }

                if (point != null) {
                    DistributorPointDetailScreen(
                        point       = point,
                        battleUuid  = uiState.battleUuid,
                        viewModel   = distributorViewModel,
                        onBackClick = { backStack.removeLastOrNull() },
                        onNextClick = { _, _ -> backStack.add(Destination.Payment) }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            // ── PAYMENT ───────────────────────────────────────────────────
            entry<Destination.Payment> {
                PaymentScreen(
                    uiState          = sharedUiState,
                    onPhoneChange    = distributorViewModel::onPhoneNumberChange,
                    onMethodSelected = distributorViewModel::onPaymentMethodChange,
                    onBackClick      = { backStack.removeLastOrNull() },
                    onNextClick      = { backStack.add(Destination.Confirmation) }
                )
            }

            // ── CONFIRMATION ──────────────────────────────────────────────
            entry<Destination.Confirmation> {
                ConfirmationScreen(
                    uiState        = sharedUiState,
                    onBackClick    = { backStack.removeLastOrNull() },
                    onStartOrder   = distributorViewModel::initOrder,
                    onStartPayment = distributorViewModel::initPayment,
                    dismissError   = distributorViewModel::dismissError,
                    viewModel      = distributorViewModel,
                    onModifyClick  = {
                        backStack.popUntil { it is Destination.DistributorDetail }
                    },
                    onConfirmClick = {
                        distributorViewModel.cleanPayment()
                        backStack.add(Destination.PaymentInitiated)
                    }
                )
            }

            // ── PAYMENT INITIATED ─────────────────────────────────────────
            entry<Destination.PaymentInitiated> {
                PaymentInitiatedScreen(
                    uiState       = sharedUiState,
                    paymentMethod = sharedUiState.selectedMethod ?: PaymentMethod.OM,
                    onDone = {
                        backStack.removeLastOrNull()
                        backStack.add(Destination.PaymentSuccess)
                    },
                    onEchec = {
                        backStack.removeLastOrNull()
                        backStack.add(Destination.PaymentSuccess)
                    }
                )
            }

            // ── PAYMENT SUCCESS / ÉCHEC ─────────────────────────────────────
            entry<Destination.PaymentSuccess> {
                val isPaymentValid = sharedUiState.isPaySuccess == PaymentStatus.SUCCESS
                PaymentSuccessScreen(
                    isSuccess = isPaymentValid,
                    errorMsg  = sharedUiState.error,
                    onCloseClick = {
                        distributorViewModel.cleanPayment()
                        pendingHomeTab = Tab.HOME
                        backStack.clear()
                        backStack.add(Destination.Home)
                    },
                    onReservationsClick = {
                        distributorViewModel.cleanPayment()
                        if (isPaymentValid) {
                            pendingHomeTab = Tab.RESERVATIONS
                            backStack.clear()
                            backStack.add(Destination.Home)
                        } else {
                            backStack.popUntil { it is Destination.Payment }
                        }
                    }
                )
            }
        }
    )
}