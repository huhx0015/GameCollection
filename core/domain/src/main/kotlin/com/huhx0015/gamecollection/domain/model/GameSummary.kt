package com.huhx0015.gamecollection.domain.model

/** Lightweight game row for browse/search lists backed by IGDB. */
data class GameSummary(
    val id: Long,
    val name: String,
    val coverImageUrl: String?,
    val summary: String?,
    val genreNames: List<String>,
)
