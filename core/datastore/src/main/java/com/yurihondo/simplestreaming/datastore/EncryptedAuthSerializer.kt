package com.yurihondo.simplestreaming.datastore

import androidx.datastore.core.Serializer
import com.yurihondo.simplestreaming.core.model.Auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class EncryptedAuthSerializer @Inject constructor(
    private val cryptoHelper: CryptoHelper,
) : Serializer<Auth> {

    override val defaultValue = Auth.empty

    override suspend fun readFrom(input: InputStream): Auth {
        val encryptedData = input.readBytes()
        if (encryptedData.isEmpty()) return defaultValue
        val decryptedString = cryptoHelper.decrypt(encryptedData).decodeToString()
        return Json.decodeFromString<Auth>(decryptedString)
    }

    override suspend fun writeTo(t: Auth, output: OutputStream) {
        withContext(Dispatchers.IO) {
            val plainData = Json.encodeToString(t).encodeToByteArray()
            val encryptedData = cryptoHelper.encrypt(plainData)
            output.write(encryptedData)
        }
    }
}
