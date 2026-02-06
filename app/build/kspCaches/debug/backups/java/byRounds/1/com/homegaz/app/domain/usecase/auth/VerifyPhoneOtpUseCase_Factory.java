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
public final class VerifyPhoneOtpUseCase_Factory implements Factory<VerifyPhoneOtpUseCase> {
  private final Provider<AuthRepository> repositoryProvider;

  public VerifyPhoneOtpUseCase_Factory(Provider<AuthRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public VerifyPhoneOtpUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static VerifyPhoneOtpUseCase_Factory create(Provider<AuthRepository> repositoryProvider) {
    return new VerifyPhoneOtpUseCase_Factory(repositoryProvider);
  }

  public static VerifyPhoneOtpUseCase newInstance(AuthRepository repository) {
    return new VerifyPhoneOtpUseCase(repository);
  }
}
