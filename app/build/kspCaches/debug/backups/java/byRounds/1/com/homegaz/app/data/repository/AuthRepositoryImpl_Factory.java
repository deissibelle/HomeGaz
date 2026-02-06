package com.homegaz.app.data.repository;

import com.homegaz.app.data.local.HomeGazDatabase;
import com.homegaz.app.data.local.PreferencesManager;
import com.homegaz.app.data.remote.HomeGazApiClient;
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<HomeGazApiClient> apiProvider;

  private final Provider<HomeGazDatabase> databaseProvider;

  private final Provider<PreferencesManager> preferencesProvider;

  public AuthRepositoryImpl_Factory(Provider<HomeGazApiClient> apiProvider,
      Provider<HomeGazDatabase> databaseProvider,
      Provider<PreferencesManager> preferencesProvider) {
    this.apiProvider = apiProvider;
    this.databaseProvider = databaseProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(apiProvider.get(), databaseProvider.get(), preferencesProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(Provider<HomeGazApiClient> apiProvider,
      Provider<HomeGazDatabase> databaseProvider,
      Provider<PreferencesManager> preferencesProvider) {
    return new AuthRepositoryImpl_Factory(apiProvider, databaseProvider, preferencesProvider);
  }

  public static AuthRepositoryImpl newInstance(HomeGazApiClient api, HomeGazDatabase database,
      PreferencesManager preferences) {
    return new AuthRepositoryImpl(api, database, preferences);
  }
}
