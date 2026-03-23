package com.huhx0015.gamecollection.domain.usecase

import com.huhx0015.gamecollection.domain.model.Genre
import com.huhx0015.gamecollection.domain.repository.IgdbRepository
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(
    private val igdbRepository: IgdbRepository,
) {
    suspend operator fun invoke(): Result<List<Genre>> = igdbRepository.getGenres()
}
