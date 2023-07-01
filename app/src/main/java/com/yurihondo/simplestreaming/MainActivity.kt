package com.yurihondo.simplestreaming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.yurihondo.simplestreaming.ui.SimpleStreamingApp
import com.yurihondo.simplestreaming.ui.theme.SimpleStreamingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleStreamingTheme {
                SimpleStreamingApp()
            }
        }
    }
}
