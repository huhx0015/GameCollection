package com.huhx0015.gamecollection.fakes

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

    override suspend fun getPlatforms(searchQuery: String?): Result<List<GamePlatform>> =
        platformsResult

    override suspend fun getGenres(): Result<List<Genre>> = genresResult

    override suspend fun fetchGamesPage(
        platformId: Long,
        offset: Int,
        limit: Int,
        searchQuery: String?,
        region: RegionFilter,
        genreIds: Set<Long>,
    ): Result<List<GameSummary>> = gamesPageResult

    override suspend fun getGameDetails(gameId: Long): Result<GameDetails> = detailsResult
}
