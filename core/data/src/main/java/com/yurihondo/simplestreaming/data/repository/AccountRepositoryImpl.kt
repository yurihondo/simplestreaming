package com.yurihondo.simplestreaming.data.repository

import kotlinx.coroutines.flow.Flow

class AccountRepositoryImpl : AccountRepository {
    override val isLoggedIn: Flow<Boolean>
        get() = TODO("Not yet implemented")
    override val accountName: Flow<String>
        get() = TODO("Not yet implemented")

    override suspend fun login() {
        TODO("Not yet implemented")
    }
}