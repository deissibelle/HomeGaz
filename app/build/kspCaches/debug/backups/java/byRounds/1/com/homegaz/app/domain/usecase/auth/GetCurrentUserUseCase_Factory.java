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
public final class GetCurrentUserUseCase_Factory implements Factory<GetCurrentUserUseCase> {
  private final Provider<AuthRepository> repositoryProvider;

  public GetCurrentUserUseCase_Factory(Provider<AuthRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetCurrentUserUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetCurrentUserUseCase_Factory create(Provider<AuthRepository> repositoryProvider) {
    return new GetCurrentUserUseCase_Factory(repositoryProvider);
  }

  public static GetCurrentUserUseCase newInstance(AuthRepository repository) {
    return new GetCurrentUserUseCase(repository);
  }
}
