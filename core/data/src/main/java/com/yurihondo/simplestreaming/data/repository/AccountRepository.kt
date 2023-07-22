package com.yurihondo.simplestreaming.data.repository

import android.app.Activity
import android.content.Intent
import com.yurihondo.simplestreaming.data.model.GoogleApiAccessToken
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    val isLoggedIn: Flow<Boolean>
    val accountName: Flow<String>
    fun <T : Activity> login(redirectActivity: Class<T>)

    fun saveNewStateFromIntent(data: Intent)

    suspend fun getAccessToken(): GoogleApiAccessToken
}