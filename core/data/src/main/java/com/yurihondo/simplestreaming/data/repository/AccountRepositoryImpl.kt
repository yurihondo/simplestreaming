package com.yurihondo.simplestreaming.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

internal class AccountRepositoryImpl @Inject constructor(
) : AccountRepository {

    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedIn: Flow<Boolean> = _isLoggedIn

    private val _accountName = MutableStateFlow("")
    override val accountName: Flow<String> = _accountName

    override suspend fun login() {
        Log.d("yuri", "login called")
    }
}