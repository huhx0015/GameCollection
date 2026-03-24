package com.huhx0015.gamecollection.domain.repository

import com.huhx0015.gamecollection.domain.model.GameDetails
import com.huhx0015.gamecollection.domain.model.GamePlatform
import com.huhx0015.gamecollection.domain.model.GameSummary
import com.huhx0015.gamecollection.domain.model.Genre

/** Remote game metadata from IGDB (platforms, genres, paged games, details). */
interface IgdbRepository {

    suspend fun fetchPlatformsPage(
        searchQuery: String?,
        offset: Int,
        limit: Int,
    ): Result<List<GamePlatform>>

    /** Resolves IGDB platform ids to names (e.g. for collection filters). Empty [ids] returns success of empty list. */
    suspend fun fetchPlatformsByIds(ids: Set<Long>): Result<List<GamePlatform>>

    suspend fun getGenres(): Result<List<Genre>>

    suspend fun fetchGamesPage(
        platformId: Long,
        offset: Int,
        limit: Int,
        searchQuery: String?,
        genreIds: Set<Long>,
    ): Result<List<GameSummary>>

    suspend fun getGameDetails(gameId: Long): Result<GameDetails>
}
