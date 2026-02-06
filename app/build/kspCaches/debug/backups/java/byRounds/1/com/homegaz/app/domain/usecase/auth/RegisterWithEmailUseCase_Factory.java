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
public final class RegisterWithEmailUseCase_Factory implements Factory<RegisterWithEmailUseCase> {
  private final Provider<AuthRepository> repositoryProvider;

  public RegisterWithEmailUseCase_Factory(Provider<AuthRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public RegisterWithEmailUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static RegisterWithEmailUseCase_Factory create(
      Provider<AuthRepository> repositoryProvider) {
    return new RegisterWithEmailUseCase_Factory(repositoryProvider);
  }

  public static RegisterWithEmailUseCase newInstance(AuthRepository repository) {
    return new RegisterWithEmailUseCase(repository);
  }
}
