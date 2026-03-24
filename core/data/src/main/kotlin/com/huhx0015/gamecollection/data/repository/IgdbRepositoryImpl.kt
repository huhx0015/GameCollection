package com.huhx0015.gamecollection.data.repository

import com.huhx0015.gamecollection.data.mapper.toDomain
import com.huhx0015.gamecollection.data.remote.mapSuccess
import com.huhx0015.gamecollection.data.remote.igdb.IgdbApi
import com.huhx0015.gamecollection.data.remote.igdb.IgdbQueryBuilder
import com.huhx0015.gamecollection.domain.model.GameDetails
import com.huhx0015.gamecollection.domain.model.GamePlatform
import com.huhx0015.gamecollection.domain.model.GameSummary
import com.huhx0015.gamecollection.domain.model.Genre
import com.huhx0015.gamecollection.domain.repository.IgdbRepository
import javax.inject.Inject
import javax.inject.Singleton

/** [IgdbRepository] backed by [IgdbApi] and DTO-to-domain mappers. */
@Singleton
class IgdbRepositoryImpl @Inject constructor(
    private val igdbApi: IgdbApi,
) : IgdbRepository {

    override suspend fun fetchPlatformsPage(
        searchQuery: String?,
        offset: Int,
        limit: Int,
    ): Result<List<GamePlatform>> = runCatching {
        val body = IgdbQueryBuilder.platformsRequestBody(searchQuery, offset, limit)
        igdbApi.platforms(body).mapSuccess { list -> list.map { it.toDomain() } }.getOrThrow()
    }

    override suspend fun fetchPlatformsByIds(ids: Set<Long>): Result<List<GamePlatform>> =
        if (ids.isEmpty()) {
            Result.success(emptyList())
        } else {
            runCatching {
                val body = IgdbQueryBuilder.platformsByIdsRequestBody(ids)
                igdbApi.platforms(body).mapSuccess { list -> list.map { it.toDomain() } }.getOrThrow()
            }
        }

    override suspend fun getGenres(): Result<List<Genre>> = runCatching {
        val body = IgdbQueryBuilder.genresRequestBody()
        igdbApi.genres(body).mapSuccess { list -> list.map { it.toDomain() } }.getOrThrow()
    }

    override suspend fun fetchGamesPage(
        platformId: Long,
        offset: Int,
        limit: Int,
        searchQuery: String?,
        genreIds: Set<Long>,
    ): Result<List<GameSummary>> = runCatching {
        val body = IgdbQueryBuilder.gamesRequestBody(
            platformId = platformId,
            offset = offset,
            limit = limit,
            searchQuery = searchQuery,
            genreIds = genreIds,
        )
        igdbApi.games(body).mapSuccess { list -> list.map { it.toDomain() } }.getOrThrow()
    }

    override suspend fun getGameDetails(gameId: Long): Result<GameDetails> = runCatching {
        val body = IgdbQueryBuilder.gameDetailsRequestBody(gameId)
        igdbApi.gameDetails(body).mapSuccess { list ->
            if (list.isEmpty()) {
                throw IllegalStateException("Game not found")
            }
            list.first().toDomain()
        }.getOrThrow()
    }
}
