package com.huhx0015.gamecollection.network.injections

import com.huhx0015.gamecollection.constants.AndroidConstants
import com.huhx0015.gamecollection.network.interfaces.RetrofitInterface
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val HTTP_CLIENT_CACHE = 10 * 1024 * 1024

    @Provides
    @Singleton
    fun providesCache(@ApplicationContext context: Context): Cache {
        return Cache(context.cacheDir, HTTP_CLIENT_CACHE.toLong())
    }

    @Provides
    @Singleton
    fun providesMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(cache: Cache): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .cache(cache)
            .build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(moshi: Moshi, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(AndroidConstants.API_URL)
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun providesRetrofitInterface(retrofit: Retrofit): RetrofitInterface {
        return retrofit.create(RetrofitInterface::class.java)
    }
}
