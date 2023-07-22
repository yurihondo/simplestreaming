package com.yurihondo.simplestreaming.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.yurihondo.simplestreaming.core.model.Auth
import com.yurihondo.simplestreaming.datastore.CryptoHelper
import com.yurihondo.simplestreaming.datastore.EncryptedAuthSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providesAuthDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Auth> {
        return DataStoreFactory.create(
            serializer = EncryptedAuthSerializer(CryptoHelper(context))
        ) {
            context.dataStoreFile("auth_preferences.pb")
        }
    }
}