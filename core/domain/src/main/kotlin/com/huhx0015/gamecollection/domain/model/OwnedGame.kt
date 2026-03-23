package com.huhx0015.gamecollection.domain.model

/**
 * A game the user saved locally; [title] and [coverUrl] are denormalized for offline list.
 */
data class OwnedGame(
    val igdbGameId: Long,
    val platformId: Long,
    val title: String,
    val coverUrl: String?,
    val addedAtEpochMillis: Long,
)
