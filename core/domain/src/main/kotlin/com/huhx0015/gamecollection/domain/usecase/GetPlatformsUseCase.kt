package com.huhx0015.gamecollection.domain.usecase

import com.huhx0015.gamecollection.domain.model.GamePlatform
import com.huhx0015.gamecollection.domain.repository.IgdbRepository
import javax.inject.Inject

/** Fetches IGDB platforms, optionally filtered by a search query. */
class GetPlatformsUseCase @Inject constructor(
    private val igdbRepository: IgdbRepository,
) {
    suspend operator fun invoke(searchQuery: String?): Result<List<GamePlatform>> =
        igdbRepository.getPlatforms(searchQuery)
}
