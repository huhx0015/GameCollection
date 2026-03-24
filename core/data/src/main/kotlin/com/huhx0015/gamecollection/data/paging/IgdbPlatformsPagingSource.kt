package com.huhx0015.gamecollection.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.huhx0015.gamecollection.domain.model.GamePlatform
import com.huhx0015.gamecollection.domain.repository.IgdbRepository

/** Paging source for IGDB platform search / browse with offset/limit. */
class IgdbPlatformsPagingSource(
    private val searchQuery: String?,
    private val igdbRepository: IgdbRepository,
) : PagingSource<Int, GamePlatform>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GamePlatform> {
        val offset = params.key ?: 0
        val pageSize = params.loadSize
        val result = igdbRepository.fetchPlatformsPage(
            searchQuery = searchQuery,
            offset = offset,
            limit = pageSize,
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

    override fun getRefreshKey(state: PagingState<Int, GamePlatform>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(state.config.pageSize)
        }
}
