package com.huhx0015.gamecollection.data.remote.twitch

import com.huhx0015.gamecollection.data.auth.TwitchCredentials
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/** Thread-safe cache of Twitch app access tokens with expiry-aware refresh. */
@Singleton
class TwitchTokenStore @Inject constructor(
    private val twitchAuthApi: TwitchAuthApi,
    private val credentials: TwitchCredentials,
) {
    private val mutex = Mutex()
    private var cachedToken: String? = null
    private var expiresAtEpochMillis: Long = 0L

    suspend fun getBearerToken(): String = mutex.withLock {
        val now = System.currentTimeMillis()
        val margin = 60_000L
        if (cachedToken != null && now < expiresAtEpochMillis - margin) {
            return@withLock cachedToken!!
        }
        val response = twitchAuthApi.getAppAccessToken(
            clientId = credentials.clientId,
            clientSecret = credentials.clientSecret,
        )
        if (!response.isSuccessful) {
            val err = try {
                response.errorBody()?.string()?.trim().orEmpty()
            } catch (_: Exception) {
                ""
            }
            throw IOException(
                buildString {
                    append("Twitch token failed (HTTP ").append(response.code()).append(")")
                    if (err.isNotEmpty()) append(": ").append(err)
                },
            )
        }
        val body = response.body()
            ?: throw IOException("Twitch token response was empty")
        cachedToken = body.accessToken
        expiresAtEpochMillis = now + body.expiresInSeconds * 1000L
        body.accessToken
    }

    fun invalidate() {
        cachedToken = null
        expiresAtEpochMillis = 0L
    }
}
