package com.yurihondo.simplestreaming.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yurihondo.simplestreaming.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeRouteViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val acceptedTermsOfUse = userRepository.acceptedTermsOfUse.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun onAcceptTermsOfUse() {
        viewModelScope.launch { userRepository.acceptTermsOfUse() }
    }
}