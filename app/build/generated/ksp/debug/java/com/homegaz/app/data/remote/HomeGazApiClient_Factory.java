package com.homegaz.app.data.remote;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class HomeGazApiClient_Factory implements Factory<HomeGazApiClient> {
  private final Provider<HttpClient> httpClientProvider;

  public HomeGazApiClient_Factory(Provider<HttpClient> httpClientProvider) {
    this.httpClientProvider = httpClientProvider;
  }

  @Override
  public HomeGazApiClient get() {
    return newInstance(httpClientProvider.get());
  }

  public static HomeGazApiClient_Factory create(Provider<HttpClient> httpClientProvider) {
    return new HomeGazApiClient_Factory(httpClientProvider);
  }

  public static HomeGazApiClient newInstance(HttpClient httpClient) {
    return new HomeGazApiClient(httpClient);
  }
}
