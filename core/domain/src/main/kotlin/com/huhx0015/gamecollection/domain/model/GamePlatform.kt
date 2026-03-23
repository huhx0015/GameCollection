package com.huhx0015.gamecollection.domain.model

data class GamePlatform(
    val id: Long,
    val name: String,
    val slug: String?,
    val summary: String?,
)
