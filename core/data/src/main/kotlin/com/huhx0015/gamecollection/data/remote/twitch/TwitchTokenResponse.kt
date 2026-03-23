package com.huhx0015.gamecollection.data.remote.twitch

import com.squareup.moshi.Json

data class TwitchTokenResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "expires_in") val expiresInSeconds: Long,
    @Json(name = "token_type") val tokenType: String,
)
