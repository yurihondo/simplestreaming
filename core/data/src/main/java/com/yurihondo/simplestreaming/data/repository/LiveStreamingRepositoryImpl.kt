package com.yurihondo.simplestreaming.data.repository

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.media.MediaCodec
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
import com.pedro.encoder.Frame
import com.pedro.encoder.audio.AudioEncoder
import com.pedro.encoder.audio.GetAacData
import com.pedro.encoder.video.FormatVideoEncoder
import com.pedro.encoder.video.GetVideoData
import com.pedro.encoder.video.VideoEncoder
import com.pedro.rtmp.rtmp.RtmpClient
import com.pedro.rtmp.utils.ConnectCheckerRtmp
import com.yurihondo.simplestreaming.data.model.GoogleApiAccessToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import javax.inject.Inject


internal class LiveStreamingRepositoryImpl @Inject constructor(
) : LiveStreamingRepository {

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    private lateinit var youtubeApi: YouTube

    override fun init(accessToken: GoogleApiAccessToken) {
        youtubeApi = YouTube.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken.value),
        ).setApplicationName("SimpleStreaming").build()
    }

    private lateinit var rtmpClient: RtmpClient
    private lateinit var videoEncoder: VideoEncoder
    private lateinit var liveBroadcast: LiveBroadcast
    private val width = 1280 // Image width
    private val height = 720 // Image height

    private suspend fun prepareYouTube(): String {
        // Insert the LiveBroadcast
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
        liveBroadcast = withContext(Dispatchers.IO) { liveBroadcastInsert.execute() }
        Log.d("YTLiveStreamingApi", "Broadcast info: $liveBroadcast.")

        // Insert the LiveStream
        val liveStreamInsert = youtubeApi.liveStreams().insert(
            listOf("snippet", "cdn"),
            LiveStream().apply {
                snippet = LiveStreamSnippet().apply {
                    title = "Test Stream"
                }
                cdn = CdnSettings().apply {
                    format = "1080p"
                    ingestionType = "rtmp"
                    frameRate = "60fps"
                    resolution = "1080p"
                }
            }
        )
        val liveStream = withContext(Dispatchers.IO) { liveStreamInsert.execute() }
        Log.d("YTLiveStreamingApi", "stream info: $liveStream.")

        // Bind the broadcast to the stream
        val liveBroadcastBind = youtubeApi.liveBroadcasts().bind(
            liveBroadcast.id,
            listOf("id", "contentDetails"),
        ).apply { streamId = liveStream.id }
        val boundBroadcast = withContext(Dispatchers.IO) { liveBroadcastBind.execute() }
        Log.d("YTLiveStreamingApi", "bound broadcast info: $boundBroadcast.")

        // Get the RTMP URL and Stream Name
        val rtmpUrl = liveStream.cdn.ingestionInfo.ingestionAddress
        val streamName = liveStream.cdn.ingestionInfo.streamName
        return "$rtmpUrl/$streamName"
    }

    override suspend fun createBroadcast() {
        val streamUrl = prepareYouTube()
        Log.d("YTLiveStreamingApi", "RTMP URL: $streamUrl.")

        val connectChecker = object : ConnectCheckerRtmp {
            override fun onAuthErrorRtmp() {
                Log.d("YTLiveStreamingApi", "auth error")
            }

            override fun onAuthSuccessRtmp() {
                Log.d("YTLiveStreamingApi", "auth success")
            }

            override fun onConnectionFailedRtmp(reason: String) {
                Log.d("YTLiveStreamingApi", "connection failed: $reason")
            }

            override fun onConnectionStartedRtmp(rtmpUrl: String) {
                Log.d("YTLiveStreamingApi", "connection started: $rtmpUrl")
            }

            override fun onConnectionSuccessRtmp() {
                Log.d("YTLiveStreamingApi", "connection success")
            }

            override fun onDisconnectRtmp() {
                Log.d("YTLiveStreamingApi", "disconnected")
            }

            override fun onNewBitrateRtmp(bitrate: Long) {
                Log.d("YTLiveStreamingApi", "new bitrate: $bitrate")
            }
        }
        rtmpClient = RtmpClient(connectChecker)

        videoEncoder = VideoEncoder(object : GetVideoData {
            override fun onSpsPpsVps(sps: ByteBuffer, pps: ByteBuffer, vps: ByteBuffer?) {
                rtmpClient.setVideoInfo(sps, pps, vps)
            }

            override fun getVideoData(h264Buffer: ByteBuffer, info: MediaCodec.BufferInfo) {
                // Send data to RTMP server here.
                Log.d(
                    "YTLiveStreamingApi",
                    "getVideoData is called, info: presentationTimeUs = ${info.presentationTimeUs}, offset = ${info.offset}, size = ${info.size}, flags = ${info.flags}"
                )

                rtmpClient.sendVideo(h264Buffer, info)
            }

            override fun onVideoFormat(mediaFormat: android.media.MediaFormat) {
                // Nothing to do here for this example.
            }
        })

        videoEncoder.prepareVideoEncoder(
            1920,
            1080,
            30,
            3000 * 1024,
            0,
            5,
            FormatVideoEncoder.SURFACE
        )
        val surface = videoEncoder.inputSurface // MediaCodecのinput surfaceを取得します
        videoEncoder.start()
        rtmpClient.setReTries(10)
        rtmpClient.connect(streamUrl)

        val audioEncoder = AudioEncoder(object : GetAacData {
            override fun getAacData(aacBuffer: ByteBuffer, info: MediaCodec.BufferInfo) {
                rtmpClient.sendAudio(aacBuffer, info)
            }

            override fun onAudioFormat(mediaFormat: android.media.MediaFormat) {
                // NOP
            }
        })
        audioEncoder.prepareAudioEncoder(128 * 1024, 44100, false, 0)
        audioEncoder.start()

        // Create a bitmap with "Test" text
        val testBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(testBitmap)
        val paint = Paint()
        paint.color = Color.BLUE
        paint.textSize = 100f
        canvas.drawText("Hello, world!", (width / 2).toFloat(), (height / 2).toFloat(), paint)

        // Stream bitmap forever
        scope.launch {
            val clearPaint = Paint().apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            }
            while (isActive) {
                // Draw the static image onto the Surface
                val canvasTemp = surface.lockCanvas(null)
                canvasTemp.drawPaint(clearPaint)
                canvasTemp.drawBitmap(testBitmap, 0f, 0f, null)
                surface.unlockCanvasAndPost(canvasTemp)
                delay(1000L / 60L) // Frame rate 60 fps
            }
        }

        // Stream silent audio forever
        scope.launch {
            val sampleRate = 44100 // Hz
            val bitDepth = 16 // bits
            val numChannels = 1
            val duration = 1.0 // seconds

            val bytesPerSample = bitDepth / 8
            val bufferSize = (sampleRate * numChannels * bytesPerSample * duration).toInt()
            val buffer = ByteBuffer.allocateDirect(bufferSize)

            // Write silence data to the buffer.
            while (buffer.hasRemaining()) {
                buffer.putShort(0)
            }
            buffer.flip()

            // send silence data to the encoder.
            while (isActive) {
                audioEncoder.inputPCMData(Frame(buffer.array(), 0, bufferSize))
                buffer.position(0) // rewind the buffer
                delay(1000L) // send 1 time per second
            }
        }
    }
}