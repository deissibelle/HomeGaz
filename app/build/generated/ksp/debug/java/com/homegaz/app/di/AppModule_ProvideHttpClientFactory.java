package com.homegaz.app.di;

import com.homegaz.app.data.local.PreferencesManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.ktor.client.HttpClient;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideHttpClientFactory implements Factory<HttpClient> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  public AppModule_ProvideHttpClientFactory(
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public HttpClient get() {
    return provideHttpClient(preferencesManagerProvider.get());
  }

  public static AppModule_ProvideHttpClientFactory create(
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new AppModule_ProvideHttpClientFactory(preferencesManagerProvider);
  }

  public static HttpClient provideHttpClient(PreferencesManager preferencesManager) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideHttpClient(preferencesManager));
  }
}
