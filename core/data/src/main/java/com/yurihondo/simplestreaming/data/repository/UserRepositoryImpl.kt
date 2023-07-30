package com.yurihondo.simplestreaming.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.yurihondo.simplestreaming.datastore.di.UserPreferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    @UserPreferencesDataStore private val dataStore: DataStore<Preferences>,
) : UserRepository {

    override val acceptedTermsOfUse = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ACCEPTED_TERMS_OF_USE] ?: false
    }.catch { exception ->
        if (exception is IllegalStateException) {
            emit(false)
        } else {
            throw exception
        }
    }

    private object PreferencesKeys {
        val ACCEPTED_TERMS_OF_USE = booleanPreferencesKey("accepted_terms_of_use")
    }

    override suspend fun acceptTermsOfUse() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCEPTED_TERMS_OF_USE] = true
        }
    }
}