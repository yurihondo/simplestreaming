package com.yurihondo.simplestreaming.text

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yurihondo.simplestreaming.domain.di.StartTextLiveStreamingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TextRouteViewModel @Inject constructor(
    private val startTextLiveStreamingUseCase: StartTextLiveStreamingUseCase,
) : ViewModel() {

    fun onStartStreaming() {
        viewModelScope.launch {
            startTextLiveStreamingUseCase.invoke()
        }
    }
}