package cm.horion.homegaz.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import cm.horion.homegaz.data.datasource.local.GazBottleLocal
import cm.horion.homegaz.data.datasource.remote.ConsumerService
import cm.horion.homegaz.data.repository.ConsumerRepositoryImpl
import cm.horion.homegaz.domain.repository.ConsumerRepository
import cm.horion.homegaz.domain.repository.GazProfileRepository
import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.domain.usecase.BuildOrderSummaryUseCase
import cm.horion.homegaz.domain.usecase.ConsumerUseCase
import cm.horion.homegaz.domain.usecase.GetDistributorDetailUseCase
import cm.horion.homegaz.domain.usecase.GetDistributorPointsUseCase
import cm.horion.homegaz.domain.usecase.LoadGazProfileUseCase
import cm.horion.homegaz.domain.usecase.RequestLocationPermissionUseCase
import cm.horion.homegaz.domain.usecase.SaveGazProfileUseCase
import cm.horion.homegaz.domain.usecase.SaveOnboardingExitUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val Context.dataStore by preferencesDataStore(name = "homegaz_prefs")

fun dataModule() = module {

    single { androidContext().dataStore }

    //service
    single { ConsumerService() }
    single { GazBottleLocal(get()) }

    // Repository
    single { UserPreferencesRepository(androidContext()) }
    single { GazProfileRepository(androidContext()) }
    single<ConsumerRepository> { ConsumerRepositoryImpl(get()) }

    // Use Cases
    factory { SaveOnboardingExitUseCase(get()) }

    factory { GetDistributorPointsUseCase() }
    factory { GetDistributorDetailUseCase() }
    factory { RequestLocationPermissionUseCase() }
    factory { BuildOrderSummaryUseCase() }
    factory { SaveGazProfileUseCase(get()) }
    factory { LoadGazProfileUseCase(get()) }
    single { ConsumerUseCase(get()) }
}
