package com.yurihondo.simplestreaming.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yurihondo.simplestreaming.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    val isLoggedIn = flowOf(false).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )

    val accountName = flowOf("").stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = "",
    )

    fun onStartLogin() {
        accountRepository.login(AuthActivity::class.java)
    }
}