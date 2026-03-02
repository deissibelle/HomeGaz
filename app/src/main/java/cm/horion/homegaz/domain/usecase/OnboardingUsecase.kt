// domain/usecase/SaveOnboardingExitUseCase.kt
package cm.horion.homegaz.domain.usecase

import cm.horion.homegaz.domain.repository.UserPreferencesRepository

class SaveOnboardingExitUseCase(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke() {
        repository.saveOnboardingCompleted(true)
    }
}
