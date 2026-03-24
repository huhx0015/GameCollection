package com.huhx0015.gamecollection.domain.usecase

import com.huhx0015.gamecollection.domain.model.GamePlatform
import com.huhx0015.gamecollection.domain.repository.IgdbRepository
import javax.inject.Inject

/** Fetches one page of IGDB platforms, optionally filtered by a search query. */
class GetPlatformsUseCase @Inject constructor(
    private val igdbRepository: IgdbRepository,
) {
    suspend operator fun invoke(
        searchQuery: String?,
        offset: Int = 0,
        limit: Int = 10,
    ): Result<List<GamePlatform>> =
        igdbRepository.fetchPlatformsPage(searchQuery, offset, limit)
}
