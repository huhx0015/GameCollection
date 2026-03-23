package com.huhx0015.gamecollection.domain.usecase

import com.huhx0015.gamecollection.domain.repository.CollectionRepository
import javax.inject.Inject

class RemoveFromCollectionUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    suspend operator fun invoke(igdbGameId: Long, platformId: Long): Result<Unit> =
        collectionRepository.removeFromCollection(igdbGameId, platformId)
}
