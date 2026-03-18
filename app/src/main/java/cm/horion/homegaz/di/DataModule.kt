package cm.horion.homegaz.di

import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.domain.usecase.BuildOrderSummaryUseCase
import cm.horion.homegaz.domain.usecase.GetDistributorDetailUseCase
import cm.horion.homegaz.domain.usecase.GetDistributorPointsUseCase
import cm.horion.homegaz.domain.usecase.RequestLocationPermissionUseCase
import cm.horion.homegaz.domain.usecase.SaveOnboardingExitUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
fun dataModule() = module {
    // Repository
    single { UserPreferencesRepository(androidContext()) }
    // Use Cases
    factory { SaveOnboardingExitUseCase(get()) }

    factory { GetDistributorPointsUseCase() }
    factory { GetDistributorDetailUseCase() }
    factory { RequestLocationPermissionUseCase() }
    factory { BuildOrderSummaryUseCase() }
}
