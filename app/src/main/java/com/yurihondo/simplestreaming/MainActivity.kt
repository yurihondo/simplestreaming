package com.yurihondo.simplestreaming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.yurihondo.simplestreaming.data.repository.LiveStreamingRepository
import com.yurihondo.simplestreaming.ui.SimpleStreamingApp
import com.yurihondo.simplestreaming.ui.theme.SimpleStreamingTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var streamingRepository: LiveStreamingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleStreamingTheme {
                SimpleStreamingApp()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Stop streaming when app is in background for safety
        lifecycleScope.launch { streamingRepository.stopStreaming() }
    }
}
