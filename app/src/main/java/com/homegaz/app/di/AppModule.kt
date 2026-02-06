package com.homegaz.app.di

import android.content.Context
import androidx.room.Room
import com.homegaz.app.data.local.HomeGazDatabase
import com.homegaz.app.data.local.PreferencesManager
import com.homegaz.app.data.remote.HomeGazApiClient
import com.homegaz.app.data.remote.createHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHomeGazDatabase(
        @ApplicationContext context: Context
    ): HomeGazDatabase {
        return Room.databaseBuilder(
            context,
            HomeGazDatabase::class.java,
            HomeGazDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        preferencesManager: PreferencesManager
    ): HttpClient {
        return createHttpClient {
            runBlocking {
                preferencesManager.getAuthTokens()?.accessToken
            }
        }
    }

    @Provides
    @Singleton
    fun provideHomeGazApiClient(
        httpClient: HttpClient
    ): HomeGazApiClient {
        return HomeGazApiClient(httpClient)
    }
}
