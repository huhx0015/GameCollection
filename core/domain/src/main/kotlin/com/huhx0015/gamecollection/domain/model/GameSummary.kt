package com.huhx0015.gamecollection.domain.model

data class GameSummary(
    val id: Long,
    val name: String,
    val coverImageUrl: String?,
    val summary: String?,
    val genreNames: List<String>,
)
