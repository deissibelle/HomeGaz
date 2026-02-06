package com.homegaz.app.di;

import android.content.Context;
import com.homegaz.app.data.local.HomeGazDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppModule_ProvideHomeGazDatabaseFactory implements Factory<HomeGazDatabase> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideHomeGazDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public HomeGazDatabase get() {
    return provideHomeGazDatabase(contextProvider.get());
  }

  public static AppModule_ProvideHomeGazDatabaseFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideHomeGazDatabaseFactory(contextProvider);
  }

  public static HomeGazDatabase provideHomeGazDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideHomeGazDatabase(context));
  }
}
