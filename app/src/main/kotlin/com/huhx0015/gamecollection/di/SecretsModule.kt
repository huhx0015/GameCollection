package com.huhx0015.gamecollection.di

import com.huhx0015.gamecollection.BuildConfig
import com.huhx0015.gamecollection.data.auth.TwitchCredentials
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Supplies Twitch API credentials from Gradle-injected [BuildConfig] fields. */
@Module
@InstallIn(SingletonComponent::class)
object SecretsModule {

    @Provides
    @Singleton
    fun provideTwitchCredentials(): TwitchCredentials =
        TwitchCredentials(
            clientId = BuildConfig.TWITCH_CLIENT_ID,
            clientSecret = BuildConfig.TWITCH_CLIENT_SECRET,
        )
}
