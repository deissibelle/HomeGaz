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
public final class RegisterWithPhoneUseCase_Factory implements Factory<RegisterWithPhoneUseCase> {
  private final Provider<AuthRepository> repositoryProvider;

  public RegisterWithPhoneUseCase_Factory(Provider<AuthRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public RegisterWithPhoneUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static RegisterWithPhoneUseCase_Factory create(
      Provider<AuthRepository> repositoryProvider) {
    return new RegisterWithPhoneUseCase_Factory(repositoryProvider);
  }

  public static RegisterWithPhoneUseCase newInstance(AuthRepository repository) {
    return new RegisterWithPhoneUseCase(repository);
  }
}
