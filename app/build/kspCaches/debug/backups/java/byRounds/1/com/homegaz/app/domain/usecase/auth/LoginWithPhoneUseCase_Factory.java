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
public final class LoginWithPhoneUseCase_Factory implements Factory<LoginWithPhoneUseCase> {
  private final Provider<AuthRepository> repositoryProvider;

  public LoginWithPhoneUseCase_Factory(Provider<AuthRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public LoginWithPhoneUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static LoginWithPhoneUseCase_Factory create(Provider<AuthRepository> repositoryProvider) {
    return new LoginWithPhoneUseCase_Factory(repositoryProvider);
  }

  public static LoginWithPhoneUseCase newInstance(AuthRepository repository) {
    return new LoginWithPhoneUseCase(repository);
  }
}
