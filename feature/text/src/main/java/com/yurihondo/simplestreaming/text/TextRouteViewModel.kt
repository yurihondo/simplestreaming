package com.yurihondo.simplestreaming.text

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yurihondo.simplestreaming.data.repository.AccountRepository
import com.yurihondo.simplestreaming.data.repository.LiveStreamingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TextRouteViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val liveStreamingRepository: LiveStreamingRepository,
) : ViewModel() {

    fun onStartStreaming() {
        val token = accountRepository.getAccessToken()
        if (token.isValid()) liveStreamingRepository.init(token)
        viewModelScope.launch { liveStreamingRepository.createBroadcast() }
    }
}