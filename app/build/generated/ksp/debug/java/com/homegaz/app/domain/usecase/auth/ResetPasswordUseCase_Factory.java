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
public final class ResetPasswordUseCase_Factory implements Factory<ResetPasswordUseCase> {
  private final Provider<AuthRepository> repositoryProvider;

  public ResetPasswordUseCase_Factory(Provider<AuthRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ResetPasswordUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ResetPasswordUseCase_Factory create(Provider<AuthRepository> repositoryProvider) {
    return new ResetPasswordUseCase_Factory(repositoryProvider);
  }

  public static ResetPasswordUseCase newInstance(AuthRepository repository) {
    return new ResetPasswordUseCase(repository);
  }
}
