package com.yurihondo.simplestreaming.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Auth(
    val accessToken: String?,
    val refreshToken: String?,
) {
    companion object {
        val empty = Auth("", "")
    }
}
