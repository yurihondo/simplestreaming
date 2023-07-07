package com.yurihondo.simplestreaming.data.di

import com.yurihondo.simplestreaming.data.repository.AccountRepository
import com.yurihondo.simplestreaming.data.repository.AccountRepositoryImpl
import com.yurihondo.simplestreaming.data.repository.LiveStreamingRepository
import com.yurihondo.simplestreaming.data.repository.LiveStreamingRepositoryImpl
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
    internal abstract fun bindAccountRepository(
        impl: AccountRepositoryImpl
    ): AccountRepository

    @Binds
    @Singleton
    internal abstract fun bindLiveStreamingRepository(
        impl: LiveStreamingRepositoryImpl
    ): LiveStreamingRepository
}