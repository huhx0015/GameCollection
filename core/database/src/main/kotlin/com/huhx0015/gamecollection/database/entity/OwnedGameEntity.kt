package com.huhx0015.gamecollection.database.entity

import androidx.room.Entity

/** Room row for a game saved to the user's collection (composite key: game + platform). */
@Entity(
    tableName = "owned_games",
    primaryKeys = ["igdbGameId", "platformId"],
)
data class OwnedGameEntity(
    val igdbGameId: Long,
    val platformId: Long,
    val title: String,
    val coverUrl: String?,
    val addedAtEpochMillis: Long,
)
