package com.huhx0015.gamecollection.domain.fakes

import com.huhx0015.gamecollection.domain.model.GameDetails
import com.huhx0015.gamecollection.domain.model.GamePlatform
import com.huhx0015.gamecollection.domain.model.GameSummary
import com.huhx0015.gamecollection.domain.model.Genre
import com.huhx0015.gamecollection.domain.model.RegionFilter
import com.huhx0015.gamecollection.domain.repository.IgdbRepository

class FakeIgdbRepository : IgdbRepository {

    var platformsResult: Result<List<GamePlatform>> = Result.success(emptyList())
    var genresResult: Result<List<Genre>> = Result.success(emptyList())
    var gamesPageResult: Result<List<GameSummary>> = Result.success(emptyList())
    var detailsResult: Result<GameDetails> = Result.failure(IllegalStateException("no details"))

    var lastPlatformsQuery: String? = null
    var lastGamesPageArgs: GamesPageArgs? = null
    var lastDetailsId: Long? = null

    override suspend fun getPlatforms(searchQuery: String?): Result<List<GamePlatform>> {
        lastPlatformsQuery = searchQuery
        return platformsResult
    }

    override suspend fun getGenres(): Result<List<Genre>> = genresResult

    override suspend fun fetchGamesPage(
        platformId: Long,
        offset: Int,
        limit: Int,
        searchQuery: String?,
        region: RegionFilter,
        genreIds: Set<Long>,
    ): Result<List<GameSummary>> {
        lastGamesPageArgs = GamesPageArgs(platformId, offset, limit, searchQuery, region, genreIds)
        return gamesPageResult
    }

    override suspend fun getGameDetails(gameId: Long): Result<GameDetails> {
        lastDetailsId = gameId
        return detailsResult
    }

    data class GamesPageArgs(
        val platformId: Long,
        val offset: Int,
        val limit: Int,
        val searchQuery: String?,
        val region: RegionFilter,
        val genreIds: Set<Long>,
    )
}
