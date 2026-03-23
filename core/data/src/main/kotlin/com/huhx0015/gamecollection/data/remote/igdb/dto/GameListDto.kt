package com.huhx0015.gamecollection.data.remote.igdb.dto

data class GameListDto(
    val id: Long,
    val name: String,
    val summary: String?,
    val genres: List<GenreNameDto>?,
    val cover: CoverDto?,
)
