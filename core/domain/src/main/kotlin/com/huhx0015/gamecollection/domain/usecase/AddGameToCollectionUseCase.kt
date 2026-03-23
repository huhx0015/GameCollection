package com.huhx0015.gamecollection.domain.usecase

import com.huhx0015.gamecollection.domain.model.GameDetails
import com.huhx0015.gamecollection.domain.model.OwnedGame
import com.huhx0015.gamecollection.domain.repository.CollectionRepository
import javax.inject.Inject

class AddGameToCollectionUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    suspend operator fun invoke(details: GameDetails, platformId: Long): Result<Unit> {
        val owned = OwnedGame(
            igdbGameId = details.id,
            platformId = platformId,
            title = details.name,
            coverUrl = details.coverImageUrl,
            addedAtEpochMillis = System.currentTimeMillis(),
        )
        return collectionRepository.addToCollection(owned)
    }
}
