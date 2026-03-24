package com.huhx0015.gamecollection.domain.usecase

import com.huhx0015.gamecollection.domain.repository.CollectionRepository
import javax.inject.Inject

/** Returns whether a game on a given platform is already saved locally. */
class IsGameInCollectionUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    suspend operator fun invoke(igdbGameId: Long, platformId: Long): Boolean =
        collectionRepository.isInCollection(igdbGameId, platformId)
}
