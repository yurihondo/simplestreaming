package com.yurihondo.simplestreaming.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.yurihondo.simplestreaming.datastore.CryptoHelper
import com.yurihondo.simplestreaming.datastore.EncryptedAuthStateSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthState
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providesAuthDataStore(
        @ApplicationContext context: Context,
    ): DataStore<AuthState> {
        return DataStoreFactory.create(
            serializer = EncryptedAuthStateSerializer(CryptoHelper(context))
        ) {
            context.dataStoreFile("auth_preferences")
        }
    }

    @UserPreferencesDataStore
    @Provides
    @Singleton
    fun provideUserDateStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("user")
        }
    }
}