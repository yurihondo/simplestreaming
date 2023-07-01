package com.yurihondo.simplestreaming.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.openid.appauth.AuthorizationResponse

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authorizationResponse = AuthorizationResponse.fromIntent(intent)
    }
}