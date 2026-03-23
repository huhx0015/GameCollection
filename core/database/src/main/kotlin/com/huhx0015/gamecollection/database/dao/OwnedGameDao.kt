package com.huhx0015.gamecollection.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.huhx0015.gamecollection.database.entity.OwnedGameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OwnedGameDao {

    @Query(
        """
        SELECT * FROM owned_games
        WHERE (:platformFilter < 0 OR platformId = :platformFilter)
        ORDER BY addedAtEpochMillis DESC
        """,
    )
    fun observeOwned(platformFilter: Long): Flow<List<OwnedGameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: OwnedGameEntity)

    @Query(
        """
        DELETE FROM owned_games
        WHERE igdbGameId = :igdbGameId AND platformId = :platformId
        """,
    )
    suspend fun delete(igdbGameId: Long, platformId: Long)

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM owned_games
            WHERE igdbGameId = :igdbGameId AND platformId = :platformId
        )
        """,
    )
    suspend fun exists(igdbGameId: Long, platformId: Long): Boolean
}
