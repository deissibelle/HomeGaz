package com.homegaz.app.di

import com.homegaz.app.data.repository.AuthRepositoryImpl
import com.homegaz.app.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    // Add other repository bindings here as you create them:
    // @Binds
    // @Singleton
    // abstract fun bindGasPointRepository(
    //     gasPointRepositoryImpl: GasPointRepositoryImpl
    // ): GasPointRepository
}
