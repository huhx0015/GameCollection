package com.huhx0015.gamecollection.domain.model

/** Full IGDB game payload for the detail screen (text, art, genres, release metadata). */
data class GameDetails(
    val id: Long,
    val name: String,
    val summary: String?,
    val storyline: String?,
    val coverImageUrl: String?,
    val screenshotUrls: List<String>,
    val genreNames: List<String>,
    val firstReleaseDate: Long?,
)
