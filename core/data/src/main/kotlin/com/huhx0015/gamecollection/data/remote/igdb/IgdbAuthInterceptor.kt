package com.huhx0015.gamecollection.data.remote.igdb

import com.huhx0015.gamecollection.data.auth.TwitchCredentials
import com.huhx0015.gamecollection.data.remote.twitch.TwitchTokenStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class IgdbAuthInterceptor @Inject constructor(
    private val credentials: TwitchCredentials,
    private val tokenStore: TwitchTokenStore,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = try {
            runBlocking { tokenStore.getBearerToken() }
        } catch (e: IOException) {
            throw e
        } catch (e: Exception) {
            throw IOException("Failed to obtain access token: ${e.message}", e)
        }
        val request = chain.request().newBuilder()
            .header("Client-ID", credentials.clientId)
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
