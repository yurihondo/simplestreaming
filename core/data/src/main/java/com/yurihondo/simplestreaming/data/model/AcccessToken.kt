package com.yurihondo.simplestreaming.data.model

@JvmInline
value class GoogleApiAccessToken(
    val value: String,
) {
    fun isValid() = value.isNotEmpty()
}