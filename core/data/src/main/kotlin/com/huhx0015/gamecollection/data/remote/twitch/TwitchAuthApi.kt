package com.huhx0015.gamecollection.data.remote.twitch

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/** Retrofit service for Twitch client-credentials token requests. */
interface TwitchAuthApi {

    @FormUrlEncoded
    @POST("oauth2/token")
    suspend fun getAppAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String = "client_credentials",
    ): Response<TwitchTokenResponse>
}
