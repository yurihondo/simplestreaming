package com.yurihondo.simplestreaming.data.repository

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.media.MediaCodec
import android.util.Log
import com.google.api.client.auth.oauth2.BearerToken
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.CdnSettings
import com.google.api.services.youtube.model.LiveBroadcast
import com.google.api.services.youtube.model.LiveBroadcastContentDetails
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.coroutines.coroutineContext


internal class LiveStreamingRepositoryImpl @Inject constructor(
) : LiveStreamingRepository {

    private val _isStreaming = MutableStateFlow(false)
    override val isStreaming = _isStreaming.asStateFlow()

    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val width = 1920 // Image width
    private val height = 1080 // Image height
    private lateinit var youtubeApi: YouTube

    private var rtmpClient: RtmpClient? = null
    private var videoEncoder: VideoEncoder? = null
    private var audioEncoder: AudioEncoder? = null
    private var liveBroadcast: LiveBroadcast? = null
    private var streamedBitmap: Bitmap? = null
    private var videoJob: Job? = null
    private var audioJob: Job? = null
    private var broadcastStatusJob: Job? = null


    override fun init(accessToken: GoogleApiAccessToken) {
        youtubeApi = YouTube.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken.value),
        ).setApplicationName("SimpleStreaming").build()
    }

    private suspend fun prepareYouTube(): String {
        Log.d("YTLiveStreamingApi", "prepareYouTube is called.")

        // Insert the LiveBroadcast
        val liveBroadcastInsert = youtubeApi.liveBroadcasts().insert(
            listOf("snippet", "status", "contentDetails"),
            LiveBroadcast().apply {
                snippet = LiveBroadcastSnippet().apply {
                    title = "Test Broadcast"
                    scheduledStartTime = DateTime(System.currentTimeMillis())
                    description = "This is a test broadcast"
                }
                status = LiveBroadcastStatus().apply {
                    privacyStatus = "unlisted"
                    selfDeclaredMadeForKids = false
                }
                contentDetails = LiveBroadcastContentDetails().apply {
                    enableAutoStop = true
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
            liveBroadcast?.id,
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
                _isStreaming.value = true
            }

            override fun onDisconnectRtmp() {
                Log.d("YTLiveStreamingApi", "disconnected")
                _isStreaming.value = false
            }

            override fun onNewBitrateRtmp(bitrate: Long) {
                Log.d("YTLiveStreamingApi", "new bitrate: $bitrate")
            }
        }
        rtmpClient = RtmpClient(connectChecker)

        videoEncoder = VideoEncoder(object : GetVideoData {
            override fun onSpsPpsVps(sps: ByteBuffer, pps: ByteBuffer, vps: ByteBuffer?) {
                rtmpClient?.setVideoInfo(sps, pps, vps)
            }

            override fun getVideoData(h264Buffer: ByteBuffer, info: MediaCodec.BufferInfo) {
                // Send data to RTMP server here.
                Log.d(
                    "YTLiveStreamingApi",
                    "getVideoData is called, info: presentationTimeUs = ${info.presentationTimeUs}, offset = ${info.offset}, size = ${info.size}, flags = ${info.flags}"
                )

                rtmpClient?.sendVideo(h264Buffer, info)
            }

            override fun onVideoFormat(mediaFormat: android.media.MediaFormat) {
                // Nothing to do here for this example.
            }
        })

        videoEncoder?.prepareVideoEncoder(
            1920,
            1080,
            30,
            3000 * 1024,
            0,
            5,
            FormatVideoEncoder.SURFACE
        )
        val surface = videoEncoder?.inputSurface // MediaCodecのinput surfaceを取得します
        videoEncoder?.start()
        rtmpClient?.setReTries(10)
        rtmpClient?.connect(streamUrl)

        audioEncoder = AudioEncoder(object : GetAacData {
            override fun getAacData(aacBuffer: ByteBuffer, info: MediaCodec.BufferInfo) {
                rtmpClient?.sendAudio(aacBuffer, info)
            }

            override fun onAudioFormat(mediaFormat: android.media.MediaFormat) {
                // NOP
            }
        })
        audioEncoder?.prepareAudioEncoder(128 * 1024, 44100, false, 0)
        audioEncoder?.start()

        // Stream bitmap forever
        videoJob?.cancel()
        videoJob = null
        videoJob = scope.launch {
            val clearPaint = Paint().apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            }
            while (isActive) {
                streamedBitmap?.let { bitmap ->
                    // Draw the static image onto the Surface
                    val canvasTemp = surface?.lockCanvas(null)
                    canvasTemp?.drawPaint(clearPaint)
                    canvasTemp?.drawBitmap(bitmap, 0f, 0f, null)
                    surface?.unlockCanvasAndPost(canvasTemp)
                    delay(1000L / 60L) // Frame rate 60 fps
                }
            }
        }

        // Stream silent audio forever
        audioJob?.cancel()
        audioJob = null
        audioJob = scope.launch {
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
                audioEncoder?.inputPCMData(Frame(buffer.array(), 0, bufferSize))
                buffer.position(0) // rewind the buffer
                delay(1000L) // send 1 time per second
            }
        }

        broadcastStatusJob?.cancel()
        broadcastStatusJob = null
        broadcastStatusJob = scope.launch {
            fun transitionStatus(next: String) {
                youtubeApi.liveBroadcasts().transition(next, liveBroadcast?.id, listOf("snippet", "status")).execute()
            }

            var status: String? = null

            repeat(12) {
                delay(5000L)
                try {
                    // ライブ配信の情報を取得
                    val liveBroadcastList = youtubeApi.liveBroadcasts().list(listOf("snippet", "status"))
                        .setId(listOf(liveBroadcast?.id))
                        .execute()
                    status = liveBroadcastList.items.firstOrNull()?.status?.lifeCycleStatus

                    // ライブ配信が存在し、かつ状態が "ready" > "testing" > "live"へ変更
                    when (status) {
                        "ready" -> transitionStatus("testing")
                        "testing" -> transitionStatus("live")
                        "live" -> return@repeat
                    }
                } catch (e: GoogleJsonResponseException) {
                    Log.e("YTLiveStreamingApi", "Error while monitoring broadcast status: ${e.message}", e)
                }
            }

            if (status != "live") {
                stopStreaming()
            }
        }
    }

    override suspend fun updateStreamingText(text: String) {
        updateStreamImage(text)
    }

    override suspend fun startStreaming() {
        TODO("Not yet implemented")
    }

    override suspend fun stopStreaming() {
        withContext(Dispatchers.IO) {
            // Change status to "complete" if possible
            broadcastStatusJob?.cancel()
            broadcastStatusJob = null
            liveBroadcast?.let { broadcast ->
                val liveBroadcastList = youtubeApi.liveBroadcasts().list(listOf("snippet", "status"))
                    .setId(listOf(broadcast.id))
                    .execute()
                val status = liveBroadcastList.items.firstOrNull()?.status?.lifeCycleStatus
                if (status == "live") {
                    youtubeApi.liveBroadcasts().transition("complete", broadcast.id, listOf("snippet", "status")).execute()
                }
            }
            liveBroadcast = null

            // Stop video streaming
            videoJob?.cancel()
            videoJob = null
            videoEncoder?.stop()
            videoEncoder = null
            streamedBitmap?.recycle()
            streamedBitmap = null

            // Stop audio streaming
            audioJob?.cancel()
            audioJob = null
            audioEncoder?.stop()
            audioEncoder = null

            // Disconnect from RTMP server
            rtmpClient?.disconnect()
            rtmpClient = null
        }
    }


    private suspend fun updateStreamImage(text: String) {
        withContext(coroutineContext) {
            val bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bm)
            val paint = Paint()

            val strokeWidth = 5f  // 枠線の太さを設定
            paint.color = Color.WHITE  // 枠線の色を設定
            paint.style = Paint.Style.STROKE  // 描画モードをSTROKEに設定して、塗りつぶしをしないようにする
            paint.strokeWidth = strokeWidth  // 枠線の太さを設定
            canvas.drawRect(strokeWidth/2, strokeWidth/2, width - strokeWidth/2, height - strokeWidth/2, paint)  // 枠線を描画

            paint.color = Color.WHITE  // テキストの色を設定
            paint.style = Paint.Style.FILL  // 描画モードをFILLに変更してテキストを塗りつぶし
            paint.textSize = 100f
            val bounds = Rect()
            paint.getTextBounds(text, 0, text.length, bounds)
            val x = (width - bounds.width()) / 2.0f
            val y = (height + bounds.height()) / 2.0f
            canvas.drawText(text, x, y, paint)

            streamedBitmap = bm
        }
    }
}