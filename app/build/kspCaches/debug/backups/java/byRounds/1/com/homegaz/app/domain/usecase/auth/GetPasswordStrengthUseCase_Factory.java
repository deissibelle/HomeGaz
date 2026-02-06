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
public final class GetPasswordStrengthUseCase_Factory implements Factory<GetPasswordStrengthUseCase> {
  private final Provider<AuthRepository> repositoryProvider;

  public GetPasswordStrengthUseCase_Factory(Provider<AuthRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetPasswordStrengthUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetPasswordStrengthUseCase_Factory create(
      Provider<AuthRepository> repositoryProvider) {
    return new GetPasswordStrengthUseCase_Factory(repositoryProvider);
  }

  public static GetPasswordStrengthUseCase newInstance(AuthRepository repository) {
    return new GetPasswordStrengthUseCase(repository);
  }
}
