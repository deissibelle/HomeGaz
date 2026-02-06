package com.homegaz.app.presentation.home;

import com.homegaz.app.domain.usecase.auth.GetCurrentUserUseCase;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<GetCurrentUserUseCase> getCurrentUserUseCaseProvider;

  public HomeViewModel_Factory(Provider<GetCurrentUserUseCase> getCurrentUserUseCaseProvider) {
    this.getCurrentUserUseCaseProvider = getCurrentUserUseCaseProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(getCurrentUserUseCaseProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<GetCurrentUserUseCase> getCurrentUserUseCaseProvider) {
    return new HomeViewModel_Factory(getCurrentUserUseCaseProvider);
  }

  public static HomeViewModel newInstance(GetCurrentUserUseCase getCurrentUserUseCase) {
    return new HomeViewModel(getCurrentUserUseCase);
  }
}
