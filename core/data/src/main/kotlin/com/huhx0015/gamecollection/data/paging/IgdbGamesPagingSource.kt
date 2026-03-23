package com.huhx0015.gamecollection.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.huhx0015.gamecollection.domain.model.GameSummary
import com.huhx0015.gamecollection.domain.model.RegionFilter
import com.huhx0015.gamecollection.domain.repository.IgdbRepository

class IgdbGamesPagingSource(
    private val platformId: Long,
    private val searchQuery: String?,
    private val region: RegionFilter,
    private val genreIds: Set<Long>,
    private val igdbRepository: IgdbRepository,
) : PagingSource<Int, GameSummary>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GameSummary> {
        val offset = params.key ?: 0
        val pageSize = params.loadSize
        val result = igdbRepository.fetchGamesPage(
            platformId = platformId,
            offset = offset,
            limit = pageSize,
            searchQuery = searchQuery,
            region = region,
            genreIds = genreIds,
        )
        return result.fold(
            onSuccess = { items ->
                val nextKey = if (items.size < pageSize) null else offset + items.size
                LoadResult.Page(
                    data = items,
                    prevKey = if (offset == 0) null else (offset - pageSize).coerceAtLeast(0),
                    nextKey = nextKey,
                )
            },
            onFailure = { LoadResult.Error(it) },
        )
    }

    override fun getRefreshKey(state: PagingState<Int, GameSummary>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(state.config.pageSize)
        }
}
