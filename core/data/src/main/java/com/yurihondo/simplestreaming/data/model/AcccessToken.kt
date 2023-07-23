package com.yurihondo.simplestreaming.data.model

@JvmInline
value class GoogleApiAccessToken(
    val value: String,
) {

    companion object {
        val invalid = GoogleApiAccessToken("")
    }

    fun isValid() = this != invalid
}