package com.huhx0015.gamecollection.data.auth

/** Twitch developer app credentials required for IGDB (Client-ID and app access token flow). */
data class TwitchCredentials(
    val clientId: String,
    val clientSecret: String,
)
