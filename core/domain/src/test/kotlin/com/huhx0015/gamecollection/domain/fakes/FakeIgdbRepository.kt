package com.huhx0015.gamecollection.domain.fakes

import com.huhx0015.gamecollection.domain.model.GameDetails
import com.huhx0015.gamecollection.domain.model.GamePlatform
import com.huhx0015.gamecollection.domain.model.GameSummary
import com.huhx0015.gamecollection.domain.model.Genre
import com.huhx0015.gamecollection.domain.repository.IgdbRepository

/** Configurable stub [IgdbRepository] for domain unit tests. */
class FakeIgdbRepository : IgdbRepository {

    var platformsResult: Result<List<GamePlatform>> = Result.success(emptyList())
    var genresResult: Result<List<Genre>> = Result.success(emptyList())
    var gamesPageResult: Result<List<GameSummary>> = Result.success(emptyList())
    var detailsResult: Result<GameDetails> = Result.failure(IllegalStateException("no details"))

    var lastPlatformsPageArgs: PlatformsPageArgs? = null
    var lastGamesPageArgs: GamesPageArgs? = null
    var lastDetailsId: Long? = null

    override suspend fun fetchPlatformsPage(
        searchQuery: String?,
        offset: Int,
        limit: Int,
    ): Result<List<GamePlatform>> {
        lastPlatformsPageArgs = PlatformsPageArgs(searchQuery, offset, limit)
        val all = platformsResult.getOrElse { return platformsResult }
        return Result.success(all.drop(offset).take(limit))
    }

    override suspend fun fetchPlatformsByIds(ids: Set<Long>): Result<List<GamePlatform>> {
        val all = platformsResult.getOrElse { return platformsResult }
        val idSet = ids.toSet()
        return Result.success(all.filter { it.id in idSet })
    }

    override suspend fun getGenres(): Result<List<Genre>> = genresResult

    override suspend fun fetchGamesPage(
        platformId: Long,
        offset: Int,
        limit: Int,
        searchQuery: String?,
        genreIds: Set<Long>,
    ): Result<List<GameSummary>> {
        lastGamesPageArgs = GamesPageArgs(platformId, offset, limit, searchQuery, genreIds)
        return gamesPageResult
    }

    override suspend fun getGameDetails(gameId: Long): Result<GameDetails> {
        lastDetailsId = gameId
        return detailsResult
    }

    data class PlatformsPageArgs(
        val searchQuery: String?,
        val offset: Int,
        val limit: Int,
    )

    data class GamesPageArgs(
        val platformId: Long,
        val offset: Int,
        val limit: Int,
        val searchQuery: String?,
        val genreIds: Set<Long>,
    )
}
