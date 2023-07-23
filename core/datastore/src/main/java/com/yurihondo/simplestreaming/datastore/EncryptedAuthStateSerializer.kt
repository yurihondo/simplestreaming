package com.yurihondo.simplestreaming.datastore

import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthState
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class EncryptedAuthStateSerializer @Inject constructor(
    private val cryptoHelper: CryptoHelper,
) : Serializer<AuthState> {

    override val defaultValue: AuthState = AuthState.jsonDeserialize("{}")

    override suspend fun readFrom(input: InputStream): AuthState {
        val encryptedData = input.readBytes()
        if (encryptedData.isEmpty()) return defaultValue
        val decryptedString = cryptoHelper.decrypt(encryptedData).decodeToString()
        return AuthState.jsonDeserialize(decryptedString)
    }

    override suspend fun writeTo(t: AuthState, output: OutputStream) {
        withContext(Dispatchers.IO) {
            val plainData = t.jsonSerializeString().encodeToByteArray()
            val encryptedData = cryptoHelper.encrypt(plainData)
            output.write(encryptedData)
        }
    }
}
