package com.huhx0015.gamecollection.data.remote.igdb.dto

/** IGDB `platforms` row used for platform picker and filters. */
data class PlatformDto(
    val id: Long,
    val name: String,
    val slug: String?,
    val summary: String?,
)
