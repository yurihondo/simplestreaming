package com.yurihondo.simplestreaming.data.repository

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.datastore.core.DataStore
import com.yurihondo.simplestreaming.data.model.GoogleApiAccessToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationService.TokenResponseCallback
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import javax.inject.Inject

internal class AccountRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authDataStore: DataStore<AuthState>,
) : AccountRepository {

    companion object {
        private const val AUTHORIZATION_EP_URI = "https://accounts.google.com/o/oauth2/v2/auth"
        private const val TOKEN_EP_URI = "https://oauth2.googleapis.com/token"
        private const val CLIENT_ID = ""
        private const val REDIRECT_PATH = "/google"
        private const val SCOPE = "https://www.googleapis.com/auth/youtube"
    }

    override val isLoggedIn = authDataStore.data.map { auth -> auth.isAuthorized }

    private val _accountName = MutableStateFlow("")
    override val accountName = _accountName.asStateFlow()

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val authService: AuthorizationService = AuthorizationService(context)
    private var appAuthState: AuthState = AuthState.jsonDeserialize("{}")

    init {
        // restore state if possible
        coroutineScope.launch {
            authDataStore.data.firstOrNull()?.let { state ->
                appAuthState = state
            }
        }
    }

    override fun <T : Activity> login(redirectActivity: Class<T>) {
        val request = AuthorizationRequest
            .Builder(
                AuthorizationServiceConfiguration(
                    Uri.parse(AUTHORIZATION_EP_URI),
                    Uri.parse(TOKEN_EP_URI)
                ),
                CLIENT_ID,
                ResponseTypeValues.CODE,
                Uri.parse("${context.packageName}:$REDIRECT_PATH")
            )
            .setScope(SCOPE)
            .build()

        val postAuthorizationIntent = Intent(context, redirectActivity)
        val pendingIntent = PendingIntent.getActivity(
            context,
            request.hashCode(),
            postAuthorizationIntent,
            PendingIntent.FLAG_MUTABLE
        )
        authService.performAuthorizationRequest(request, pendingIntent, pendingIntent)
    }

    override fun saveNewStateFromIntent(data: Intent) {
        val res = AuthorizationResponse.fromIntent(data)
        val ex = AuthorizationException.fromIntent(data)
        appAuthState.updateAndSave(res, ex)

        if (res != null && ex == null) {
            authService.performTokenRequest(res.createTokenExchangeRequest()) { tokenResponse, exception ->
                appAuthState.updateAndSave(tokenResponse, exception)
            }
        }
    }

    override suspend fun getAccessToken(): GoogleApiAccessToken {
        if (appAuthState.needsTokenRefresh) {
            val result = authService.performTokenRequest(appAuthState.createTokenRefreshRequest(), appAuthState.clientAuthentication)
            appAuthState.updateAndSave(result.first, result.second)
        }
        return appAuthState.accessToken?.let { GoogleApiAccessToken(it) } ?: GoogleApiAccessToken.invalid
    }

    private fun AuthState.updateAndSave(authRes: AuthorizationResponse?, authException: AuthorizationException?) {
        update(authRes, authException)
        authDataStore.save(this)
    }

    private fun AuthState.updateAndSave(tokenRes: TokenResponse?, authException: AuthorizationException?) {
        update(tokenRes, authException)
        authDataStore.save(this)
    }

    private fun DataStore<AuthState>.save(authState: AuthState) {
        coroutineScope.launch {
            updateData { _ ->
                // DataStore updates are not performed by passing the same instance, so pass a copy
                AuthState.jsonDeserialize(authState.jsonSerializeString())
            }
        }
    }

    private suspend fun AuthorizationService.performTokenRequest(
        request: TokenRequest,
        clientAuth: ClientAuthentication,
    ) = suspendCancellableCoroutine { continuation ->
        val callback = TokenResponseCallback { response, ex ->
            continuation.resumeWith(Result.success(response to ex))
        }
        // Register callback with an API
        performTokenRequest(request, clientAuth, callback)
    }
}