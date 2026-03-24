package com.huhx0015.gamecollection.domain.repository

import com.huhx0015.gamecollection.domain.model.GameDetails
import com.huhx0015.gamecollection.domain.model.GamePlatform
import com.huhx0015.gamecollection.domain.model.GameSummary
import com.huhx0015.gamecollection.domain.model.Genre
import com.huhx0015.gamecollection.domain.model.RegionFilter

/** Remote game metadata from IGDB (platforms, genres, paged games, details). */
interface IgdbRepository {

    suspend fun getPlatforms(searchQuery: String?): Result<List<GamePlatform>>

    suspend fun getGenres(): Result<List<Genre>>

    suspend fun fetchGamesPage(
        platformId: Long,
        offset: Int,
        limit: Int,
        searchQuery: String?,
        region: RegionFilter,
        genreIds: Set<Long>,
    ): Result<List<GameSummary>>

    suspend fun getGameDetails(gameId: Long): Result<GameDetails>
}
