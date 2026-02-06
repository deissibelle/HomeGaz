package com.homegaz.app.presentation.onboarding;

import com.homegaz.app.data.local.PreferencesManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  public OnboardingViewModel_Factory(Provider<PreferencesManager> preferencesManagerProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(preferencesManagerProvider.get());
  }

  public static OnboardingViewModel_Factory create(
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new OnboardingViewModel_Factory(preferencesManagerProvider);
  }

  public static OnboardingViewModel newInstance(PreferencesManager preferencesManager) {
    return new OnboardingViewModel(preferencesManager);
  }
}
