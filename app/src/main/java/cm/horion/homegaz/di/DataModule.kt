package cm.horion.homegaz.di

import cm.horion.homegaz.domain.repository.UserPreferencesRepository
import cm.horion.homegaz.domain.usecase.SaveOnboardingExitUseCase
import org.koin.dsl.module
fun dataModule() = module {
    // Repository
    single { UserPreferencesRepository(get()) }
    
    // Use Cases
    factory { SaveOnboardingExitUseCase(get()) }
}
