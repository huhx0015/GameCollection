package com.huhx0015.gamecollection.data.repository

import com.huhx0015.gamecollection.data.mapper.toDomain
import com.huhx0015.gamecollection.data.mapper.toEntity
import com.huhx0015.gamecollection.database.dao.OwnedGameDao
import com.huhx0015.gamecollection.domain.model.OwnedGame
import com.huhx0015.gamecollection.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** [CollectionRepository] implementation using Room [OwnedGameDao]. */
@Singleton
class CollectionRepositoryImpl @Inject constructor(
    private val ownedGameDao: OwnedGameDao,
) : CollectionRepository {

    override fun observeOwnedGames(platformFilter: Long?): Flow<List<OwnedGame>> {
        val filter = platformFilter ?: -1L
        return ownedGameDao.observeOwned(filter).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun addToCollection(game: OwnedGame): Result<Unit> = runCatching {
        ownedGameDao.upsert(game.toEntity())
    }

    override suspend fun removeFromCollection(igdbGameId: Long, platformId: Long): Result<Unit> =
        runCatching {
            ownedGameDao.delete(igdbGameId, platformId)
        }

    override suspend fun isInCollection(igdbGameId: Long, platformId: Long): Boolean =
        ownedGameDao.exists(igdbGameId, platformId)
}
