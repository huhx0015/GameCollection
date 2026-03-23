package com.huhx0015.gamecollection.database.entity

import androidx.room.Entity

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
