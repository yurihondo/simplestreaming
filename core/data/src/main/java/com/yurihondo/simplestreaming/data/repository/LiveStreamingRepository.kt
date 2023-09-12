package com.yurihondo.simplestreaming.data.repository

import com.yurihondo.simplestreaming.data.model.GoogleApiAccessToken
import kotlinx.coroutines.flow.Flow

interface LiveStreamingRepository {

    val isStreaming: Flow<Boolean>

    fun init(accessToken: GoogleApiAccessToken)

    suspend fun startStreaming()

    suspend fun stopStreaming()

    suspend fun updateStreamingText(text: String)
}