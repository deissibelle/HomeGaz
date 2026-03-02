package cm.horion.homegaz.di
fun viewModelModule() = module {
    viewModel { OnboardingViewModel(get()) }
}
