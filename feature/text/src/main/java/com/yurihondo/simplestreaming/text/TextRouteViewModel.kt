package com.yurihondo.simplestreaming.text

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yurihondo.simplestreaming.data.repository.AccountRepository
import com.yurihondo.simplestreaming.data.repository.LiveStreamingRepository
import com.yurihondo.simplestreaming.domain.di.StartTextLiveStreamingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TextRouteViewModel @Inject constructor(
    accountRepository: AccountRepository,
    private val liveStreamingRepository: LiveStreamingRepository,
    private val startTextLiveStreamingUseCase: StartTextLiveStreamingUseCase,
) : ViewModel() {

    val isStreaming = liveStreamingRepository.isStreaming
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val isLoggedIn = accountRepository.isLoggedIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    private var inputtedText = ""

    fun onStartStreaming() {
        // TODO: Warning if text is empty
        // TODO: warning network condition(not wifi)
        viewModelScope.launch {
            startTextLiveStreamingUseCase.invoke(inputtedText)
        }
    }

    fun onInputtedTextUpdated(text: TextFieldValue) {
        inputtedText = text.text
    }

    fun onUpdateStreamingText() {
        viewModelScope.launch {
            liveStreamingRepository.updateStreamingText(inputtedText)
        }
    }

    fun onStopStreaming() {
        viewModelScope.launch {
            liveStreamingRepository.stopStreaming()
        }
    }
}