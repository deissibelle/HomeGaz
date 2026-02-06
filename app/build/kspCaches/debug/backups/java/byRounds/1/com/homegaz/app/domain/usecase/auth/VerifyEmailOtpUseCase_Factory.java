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
public final class VerifyEmailOtpUseCase_Factory implements Factory<VerifyEmailOtpUseCase> {
  private final Provider<AuthRepository> repositoryProvider;

  public VerifyEmailOtpUseCase_Factory(Provider<AuthRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public VerifyEmailOtpUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static VerifyEmailOtpUseCase_Factory create(Provider<AuthRepository> repositoryProvider) {
    return new VerifyEmailOtpUseCase_Factory(repositoryProvider);
  }

  public static VerifyEmailOtpUseCase newInstance(AuthRepository repository) {
    return new VerifyEmailOtpUseCase(repository);
  }
}
