package com.yurihondo.simplestreaming.data.repository

import kotlinx.coroutines.flow.Flow

interface UserRepository {

    val acceptedTermsOfUse: Flow<Boolean>

    suspend fun acceptTermsOfUse()
}