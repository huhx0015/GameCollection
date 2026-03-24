package com.huhx0015.gamecollection.data.di

import com.huhx0015.gamecollection.data.remote.ApiConstants
import com.huhx0015.gamecollection.data.remote.igdb.IgdbApi
import com.huhx0015.gamecollection.data.remote.igdb.IgdbAuthInterceptor
import com.huhx0015.gamecollection.data.remote.twitch.TwitchAuthApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

/** Qualifies the OkHttpClient used only for Twitch OAuth calls. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TwitchOkHttp

/** Qualifies the OkHttpClient wired with [IgdbAuthInterceptor] for IGDB. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IgdbOkHttp

/** Moshi, OkHttp, and Retrofit clients for Twitch auth and IGDB API. */
@Module
@InstallIn(SingletonComponent::class)
object IgdbNetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    @TwitchOkHttp
    fun provideTwitchOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideTwitchAuthApi(
        @TwitchOkHttp client: OkHttpClient,
        moshi: Moshi,
    ): TwitchAuthApi =
        Retrofit.Builder()
            .baseUrl(ApiConstants.TWITCH_AUTH_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TwitchAuthApi::class.java)

    @Provides
    @Singleton
    @IgdbOkHttp
    fun provideIgdbOkHttpClient(
        igdbAuthInterceptor: IgdbAuthInterceptor,
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(igdbAuthInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideIgdbApi(
        @IgdbOkHttp client: OkHttpClient,
        moshi: Moshi,
    ): IgdbApi =
        Retrofit.Builder()
            .baseUrl(ApiConstants.IGDB_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(IgdbApi::class.java)
}
