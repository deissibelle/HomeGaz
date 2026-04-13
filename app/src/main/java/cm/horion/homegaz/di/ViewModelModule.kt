package cm.horion.homegaz.di

import cm.horion.homegaz.presentation.viewmodel.DistributorDetailViewModel
import cm.horion.homegaz.presentation.viewmodel.GazProfileViewModel
import cm.horion.homegaz.presentation.viewmodel.HomeViewModel
import cm.horion.homegaz.presentation.viewmodel.LocationViewModel
import cm.horion.homegaz.presentation.viewmodel.OnboardingViewModel
import cm.horion.homegaz.presentation.viewmodel.PaymentViewModel
import cm.horion.homegaz.presentation.viewmodel.ReservationsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun viewModelModule() = module {
    viewModel { OnboardingViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { LocationViewModel(get()) }
    viewModel { DistributorDetailViewModel(get()) }
    viewModel { PaymentViewModel(get()) }
    viewModel { GazProfileViewModel(get(), get()) }
    viewModel { ReservationsViewModel() }
}
