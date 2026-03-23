package com.huhx0015.gamecollection.data.remote

import retrofit2.Response

/**
 * Maps a Retrofit [Response] to [Result] without throwing [retrofit2.HttpException].
 * Non-2xx responses become [Result.failure] with a message suitable for UI error state.
 */
internal fun <T, R> Response<T>.mapSuccess(mapper: (T) -> R): Result<R> {
    if (!isSuccessful) {
        val errBody = try {
            errorBody()?.string()?.trim().orEmpty()
        } catch (_: Exception) {
            ""
        }
        val msg = buildString {
            append("HTTP ").append(code())
            if (errBody.isNotEmpty()) append(": ").append(errBody)
        }
        return Result.failure(IllegalStateException(msg))
    }
    val body = body() ?: return Result.failure(IllegalStateException("Empty response body"))
    return runCatching { mapper(body) }
}
