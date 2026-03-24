package com.huhx0015.gamecollection.data.remote.igdb.dto

import com.squareup.moshi.Json

/** IGDB single-game payload for the detail query; maps to the domain game-detail model. */
data class GameDetailsDto(
    val id: Long,
    val name: String,
    val summary: String?,
    val storyline: String?,
    val genres: List<GenreNameDto>?,
    val cover: CoverDto?,
    val screenshots: List<ScreenshotDto>?,
    @Json(name = "first_release_date") val firstReleaseDate: Long?,
)
