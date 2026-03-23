package com.huhx0015.gamecollection.fakes

import com.huhx0015.gamecollection.domain.model.OwnedGame
import com.huhx0015.gamecollection.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeCollectionRepository : CollectionRepository {

    private val owned = MutableStateFlow<List<OwnedGame>>(emptyList())
    var addResult: Result<Unit> = Result.success(Unit)

    fun setOwned(games: List<OwnedGame>) {
        owned.value = games
    }

    override fun observeOwnedGames(platformFilter: Long?): Flow<List<OwnedGame>> =
        owned.map { list ->
            if (platformFilter == null) list
            else list.filter { it.platformId == platformFilter }
        }

    override suspend fun addToCollection(game: OwnedGame): Result<Unit> {
        if (addResult.isSuccess) {
            owned.value = owned.value + game
        }
        return addResult
    }

    override suspend fun removeFromCollection(igdbGameId: Long, platformId: Long): Result<Unit> {
        owned.value = owned.value.filterNot { it.igdbGameId == igdbGameId && it.platformId == platformId }
        return Result.success(Unit)
    }

    override suspend fun isInCollection(igdbGameId: Long, platformId: Long): Boolean =
        owned.value.any { it.igdbGameId == igdbGameId && it.platformId == platformId }
}
