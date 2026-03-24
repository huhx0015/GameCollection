package com.huhx0015.gamecollection.domain.usecase

import com.huhx0015.gamecollection.domain.model.OwnedGame
import com.huhx0015.gamecollection.domain.repository.CollectionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Hot flow of owned games, optionally narrowed to one platform. */
class ObserveCollectionUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    operator fun invoke(platformFilter: Long?): Flow<List<OwnedGame>> =
        collectionRepository.observeOwnedGames(platformFilter)
}
