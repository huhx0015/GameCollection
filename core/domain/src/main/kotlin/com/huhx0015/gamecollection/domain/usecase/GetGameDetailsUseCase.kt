package com.huhx0015.gamecollection.domain.usecase

import com.huhx0015.gamecollection.domain.model.GameDetails
import com.huhx0015.gamecollection.domain.repository.IgdbRepository
import javax.inject.Inject

/** Loads a single game's full metadata for the detail flow. */
class GetGameDetailsUseCase @Inject constructor(
    private val igdbRepository: IgdbRepository,
) {
    suspend operator fun invoke(gameId: Long): Result<GameDetails> =
        igdbRepository.getGameDetails(gameId)
}
