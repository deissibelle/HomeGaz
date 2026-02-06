package com.homegaz.app.di;

import com.homegaz.app.data.remote.HomeGazApiClient;
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
public final class AppModule_ProvideHomeGazApiClientFactory implements Factory<HomeGazApiClient> {
  private final Provider<HttpClient> httpClientProvider;

  public AppModule_ProvideHomeGazApiClientFactory(Provider<HttpClient> httpClientProvider) {
    this.httpClientProvider = httpClientProvider;
  }

  @Override
  public HomeGazApiClient get() {
    return provideHomeGazApiClient(httpClientProvider.get());
  }

  public static AppModule_ProvideHomeGazApiClientFactory create(
      Provider<HttpClient> httpClientProvider) {
    return new AppModule_ProvideHomeGazApiClientFactory(httpClientProvider);
  }

  public static HomeGazApiClient provideHomeGazApiClient(HttpClient httpClient) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideHomeGazApiClient(httpClient));
  }
}
