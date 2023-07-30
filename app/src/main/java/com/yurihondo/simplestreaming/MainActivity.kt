package com.yurihondo.simplestreaming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.yurihondo.simplestreaming.data.repository.UserRepository
import com.yurihondo.simplestreaming.ui.SimpleStreamingApp
import com.yurihondo.simplestreaming.ui.theme.SimpleStreamingTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleStreamingTheme {
                SimpleStreamingApp()
            }
        }
    }
}
