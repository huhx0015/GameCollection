package com.huhx0015.gamecollection.data.remote.igdb.dto

/** IGDB `games` list row; maps to the domain game-summary model. */
data class GameListDto(
    val id: Long,
    val name: String,
    val summary: String?,
    val genres: List<GenreNameDto>?,
    val cover: CoverDto?,
)
