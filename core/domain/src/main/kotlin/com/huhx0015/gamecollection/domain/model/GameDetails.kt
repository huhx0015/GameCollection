package com.huhx0015.gamecollection.domain.model

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
