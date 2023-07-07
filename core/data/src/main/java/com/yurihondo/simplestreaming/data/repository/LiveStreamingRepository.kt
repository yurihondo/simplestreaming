package com.yurihondo.simplestreaming.data.repository

import com.yurihondo.simplestreaming.data.model.GoogleApiAccessToken

interface LiveStreamingRepository {
    fun init(accessToken: GoogleApiAccessToken)
    suspend fun createBroadcast()
}