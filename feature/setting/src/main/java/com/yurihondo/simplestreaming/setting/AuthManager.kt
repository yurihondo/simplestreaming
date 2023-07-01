package com.yurihondo.simplestreaming.setting

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

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

    fun login() {
        val request = AuthorizationRequest
            .Builder(
                config,
                clientId,
                ResponseTypeValues.CODE,
                Uri.parse("${context.packageName}:$redirectPath")
            )
            .setScope(scope)
            .build()

        val postAuthorizationIntent = Intent(context, AuthActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            request.hashCode(),
            postAuthorizationIntent,
            PendingIntent.FLAG_MUTABLE
        )
        authService.performAuthorizationRequest(request, pendingIntent)
    }

}