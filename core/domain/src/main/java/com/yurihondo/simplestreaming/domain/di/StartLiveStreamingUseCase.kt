package com.yurihondo.simplestreaming.domain.di

import com.yurihondo.simplestreaming.data.repository.AccountRepository
import com.yurihondo.simplestreaming.data.repository.LiveStreamingRepository
import javax.inject.Inject

class StartTextLiveStreamingUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val liveStreamingRepository: LiveStreamingRepository,
) : suspend (String) -> Unit {

    override suspend fun invoke(initText: String) {
        val token = accountRepository.getAccessToken()
        check(token.isValid()) { "User is not logged in" }
        liveStreamingRepository.init(token)
        liveStreamingRepository.updateStreamingText(initText)
        liveStreamingRepository.createBroadcast()
    }
}