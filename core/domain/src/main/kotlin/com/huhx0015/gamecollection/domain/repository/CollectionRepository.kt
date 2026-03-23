package com.huhx0015.gamecollection.domain.repository

import com.huhx0015.gamecollection.domain.model.OwnedGame
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {

    fun observeOwnedGames(platformFilter: Long?): Flow<List<OwnedGame>>

    suspend fun addToCollection(game: OwnedGame): Result<Unit>

    suspend fun removeFromCollection(igdbGameId: Long, platformId: Long): Result<Unit>

    suspend fun isInCollection(igdbGameId: Long, platformId: Long): Boolean
}
