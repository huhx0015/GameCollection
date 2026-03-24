package com.huhx0015.gamecollection.domain.model

/** Console or platform from IGDB; selected before browsing that platform's catalog. */
data class GamePlatform(
    val id: Long,
    val name: String,
    val slug: String?,
    val summary: String?,
)
