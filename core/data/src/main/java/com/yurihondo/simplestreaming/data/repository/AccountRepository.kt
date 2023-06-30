package com.yurihondo.simplestreaming.data.repository

import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    val isLoggedIn: Flow<Boolean>
    val accountName: Flow<String>
    suspend fun login()
}