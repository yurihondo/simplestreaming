package com.yurihondo.simplestreaming.data.repository

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.yurihondo.simplestreaming.data.model.GoogleApiAccessToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import javax.inject.Inject

internal class AccountRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AccountRepository {

    companion object {
        private const val authorizationEndpointUri = "https://accounts.google.com/o/oauth2/v2/auth"
        private const val tokenEndpointUri = "https://oauth2.googleapis.com/token"
        private const val clientId = ""
        private const val redirectPath = "/google"
        private const val scope = "https://www.googleapis.com/auth/youtube"
    }

    private val appAuthState: AuthState = AuthState.jsonDeserialize("{}")
    private val authService: AuthorizationService = AuthorizationService(context)

    private val config = AuthorizationServiceConfiguration(
        Uri.parse(authorizationEndpointUri),
        Uri.parse(tokenEndpointUri)
    )

    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedIn: Flow<Boolean> = _isLoggedIn

    private val _accountName = MutableStateFlow("")
    override val accountName: Flow<String> = _accountName

    override fun <T : Activity> login(redirectActivity: Class<T>) {
        val request = AuthorizationRequest
            .Builder(
                config,
                clientId,
                ResponseTypeValues.CODE,
                Uri.parse("${context.packageName}:$redirectPath")
            )
            .setScope(scope)
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
        if (res != null && ex == null) {
            authService.performTokenRequest(res.createTokenExchangeRequest()) { tokenResponse, authorizationException ->
                appAuthState.update(tokenResponse, authorizationException)
            }
        }
        // TODO: save auth data to encrypted datastore
        // TODO: notify auth state change
    }

    override fun getAccessToken(): GoogleApiAccessToken {
        return GoogleApiAccessToken(appAuthState.accessToken.orEmpty())
    }
}