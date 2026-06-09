package cm.horion.homegaz.di

import cm.horion.homegaz.presentation.viewmodel.ConsumerViewModel
import cm.horion.homegaz.presentation.viewmodel.DistributorDetailViewModel
import cm.horion.homegaz.presentation.viewmodel.GazProfileViewModel
import cm.horion.homegaz.presentation.viewmodel.HomeViewModel
import cm.horion.homegaz.presentation.viewmodel.LocationViewModel
import cm.horion.homegaz.presentation.viewmodel.OnboardingViewModel
import cm.horion.homegaz.presentation.viewmodel.PaymentViewModel
import cm.horion.homegaz.presentation.viewmodel.ReservationsViewModel
import cm.horion.homegaz.presentation.viewmodel.ThemeViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun viewModelModule() = module {
    viewModel { OnboardingViewModel(get()) }
    viewModel { LocationViewModel(get()) }
    viewModel { DistributorDetailViewModel(get(),get(),get()) }
    viewModel { PaymentViewModel(get()) }
    viewModel { GazProfileViewModel(
        get(), get(),
        context = androidApplication()
    ) }
    viewModel { ReservationsViewModel() }
    single { ThemeViewModel(get()) }
    viewModel { HomeViewModel(androidApplication(), get()) }
    viewModel { ConsumerViewModel(androidApplication(),get(),get()) }
}
