package com.yurihondo.simplestreaming.datastore

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AesGcmKeyManager
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CryptoHelper @Inject constructor(
    @ApplicationContext context: Context
) {
    companion object {
        private const val KEYSET_NAME = "master_keyset"
        private const val PREFERENCE_FILE = "crypto_preference"
    }

    private val aead: Aead

    init {
        AeadConfig.register()
        aead = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREFERENCE_FILE)
            .withKeyTemplate(AesGcmKeyManager.aes256GcmTemplate())
            .withMasterKeyUri("android-keystore://master_key")
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)
    }

    fun encrypt(data: ByteArray): ByteArray {
        return aead.encrypt(data, null)
    }

    fun decrypt(encryptedData: ByteArray): ByteArray {
        return aead.decrypt(encryptedData, null)
    }
}
