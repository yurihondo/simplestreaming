package com.yurihondo.simplestreaming.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yurihondo.simplestreaming.data.repository.AccountRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    @Inject
    lateinit var accountRepository: AccountRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountRepository.saveNewStateFromIntent(intent)
        finish()
    }
}