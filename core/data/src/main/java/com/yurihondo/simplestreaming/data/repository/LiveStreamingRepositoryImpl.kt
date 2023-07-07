package com.yurihondo.simplestreaming.data.repository

import android.util.Log
import com.google.api.client.auth.oauth2.BearerToken
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.CdnSettings
import com.google.api.services.youtube.model.LiveBroadcast
import com.google.api.services.youtube.model.LiveBroadcastSnippet
import com.google.api.services.youtube.model.LiveBroadcastStatus
import com.google.api.services.youtube.model.LiveStream
import com.google.api.services.youtube.model.LiveStreamSnippet
import com.yurihondo.simplestreaming.data.model.GoogleApiAccessToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class LiveStreamingRepositoryImpl @Inject constructor(
) : LiveStreamingRepository {

    private lateinit var youtubeApi: YouTube

    override fun init(accessToken: GoogleApiAccessToken) {
        youtubeApi = YouTube.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken.value),
        ).setApplicationName("SimpleStreaming").build()
    }

    override suspend fun createBroadcast() {
        val liveBroadcastInsert = youtubeApi.liveBroadcasts().insert(
            listOf("snippet", "status"),
            LiveBroadcast().apply {
                snippet = LiveBroadcastSnippet().apply {
                    title = "Test Broadcast"
                    scheduledStartTime = DateTime(System.currentTimeMillis())
                    description = "This is a test broadcast"
                }
                status = LiveBroadcastStatus().apply {
                    privacyStatus = "unlisted"
                }
            }
        )
        val liveBroadcast = withContext(Dispatchers.IO) { liveBroadcastInsert.execute() }
        Log.d("YTLiveStreamingApi", "Broadcast info: $liveBroadcast.")

        val liveStreamInsert = youtubeApi.liveStreams().insert(
            listOf("snippet", "cdn"),
            LiveStream().apply {
                snippet = LiveStreamSnippet().apply {
                    title = "Test Stream"
                }
                cdn = CdnSettings().apply {
                    format = "1080p"
                    ingestionType = "rtmp"
                    frameRate = "30fps"
                    resolution = "1080p"
                }
            }
        )
        val liveStream = withContext(Dispatchers.IO) { liveStreamInsert.execute() }
        Log.d("YTLiveStreamingApi", "stream info: $liveStream.")

        val liveBroadcastBind = youtubeApi.liveBroadcasts().bind(
            liveBroadcast.id,
            listOf("id", "contentDetails"),
        ).apply { streamId = liveStream.id }
        val boundBroadcast = withContext(Dispatchers.IO) { liveBroadcastBind.execute() }
        Log.d("YTLiveStreamingApi", "bound broadcast info: $boundBroadcast.")
    }
}