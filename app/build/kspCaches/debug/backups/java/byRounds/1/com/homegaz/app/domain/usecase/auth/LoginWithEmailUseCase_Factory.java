package com.homegaz.app.domain.usecase.auth;

import com.homegaz.app.domain.repository.AuthRepository;
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
public final class LoginWithEmailUseCase_Factory implements Factory<LoginWithEmailUseCase> {
  private final Provider<AuthRepository> repositoryProvider;

  public LoginWithEmailUseCase_Factory(Provider<AuthRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public LoginWithEmailUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static LoginWithEmailUseCase_Factory create(Provider<AuthRepository> repositoryProvider) {
    return new LoginWithEmailUseCase_Factory(repositoryProvider);
  }

  public static LoginWithEmailUseCase newInstance(AuthRepository repository) {
    return new LoginWithEmailUseCase(repository);
  }
}
