package cm.horion.homegaz.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.WorkManager
import cm.horion.homegaz.data.datasource.local.GazBottleLocal
import cm.horion.homegaz.data.datasource.remote.ConsumerService
import cm.horion.homegaz.data.datasource.remote.PayService
import cm.horion.homegaz.data.datasource.remote.PaymentCheckWorker
import cm.horion.homegaz.data.repository.ConsumerRepositoryImpl
import cm.horion.homegaz.data.repository.PayRepositoryImpl
import cm.horion.homegaz.data.security.SecureStorage
import cm.horion.homegaz.data.security.UserDataStore
import cm.horion.homegaz.data.security.UserDataStoreImpl
import cm.horion.homegaz.domain.repository.ConsumerRepository
import cm.horion.homegaz.domain.repository.DisplayPreferencesRepository
import cm.horion.homegaz.domain.repository.GazProfileRepository
import cm.horion.homegaz.domain.repository.PayRepository
import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.domain.usecase.BuildOrderSummaryUseCase
import cm.horion.homegaz.domain.usecase.ConsumerUseCase
import cm.horion.homegaz.domain.usecase.DistributorUseCase
import cm.horion.homegaz.domain.usecase.GetDistributorDetailUseCase
import cm.horion.homegaz.domain.usecase.GetDistributorPointsUseCase
import cm.horion.homegaz.domain.usecase.LoadGazProfileUseCase
import cm.horion.homegaz.domain.usecase.RequestLocationPermissionUseCase
import cm.horion.homegaz.domain.usecase.SaveOnboardingExitUseCase
import cm.horion.homegaz.util.appContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

private val Context.dataStore by preferencesDataStore(name = "homegaz_prefs")

fun dataModule() = module {

    single { WorkManager.getInstance(androidContext()) }
    single { androidContext().dataStore }

    //service
    single { SecureStorage(androidContext()) }
    single { ConsumerService(get()) }
    single { GazBottleLocal(get()) }
    single { PayService() }

    worker { PaymentCheckWorker(get(), get(), get()) }

    // Repository
    single { UserPreferencesRepository(androidContext()) }
    single { GazProfileRepository(androidContext()) }
    single { DisplayPreferencesRepository(androidContext()) }
    single<ConsumerRepository> { ConsumerRepositoryImpl(get()) }
    single<PayRepository> { PayRepositoryImpl(get()) }
    single<UserDataStore> { UserDataStoreImpl(get(),get()) }
    // Use Cases
    factory { SaveOnboardingExitUseCase(get()) }


    factory { GetDistributorPointsUseCase() }
    factory { GetDistributorDetailUseCase() }
    factory { RequestLocationPermissionUseCase() }
    factory { BuildOrderSummaryUseCase() }
    factory { LoadGazProfileUseCase(get()) }
    single { ConsumerUseCase(get()) }
    single { DistributorUseCase(get()) }
}
