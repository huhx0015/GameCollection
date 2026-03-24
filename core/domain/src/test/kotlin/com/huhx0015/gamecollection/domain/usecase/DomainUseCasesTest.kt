package com.huhx0015.gamecollection.domain.usecase

import app.cash.turbine.test
import com.huhx0015.gamecollection.domain.fakes.FakeCollectionRepository
import com.huhx0015.gamecollection.domain.fakes.FakeIgdbRepository
import com.huhx0015.gamecollection.domain.model.GameDetails
import com.huhx0015.gamecollection.domain.model.GamePlatform
import com.huhx0015.gamecollection.domain.model.Genre
import com.huhx0015.gamecollection.domain.model.OwnedGame
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Unit tests for domain use cases against fake repositories. */
class DomainUseCasesTest {

    @Test
    fun getPlatforms_delegatesToRepository() = runTest {
        val igdb = FakeIgdbRepository().apply {
            platformsResult = Result.success(
                listOf(GamePlatform(1, "PS5", "ps5", null)),
            )
        }
        val useCase = GetPlatformsUseCase(igdb)
        val result = useCase("q", offset = 0, limit = 10)
        assertEquals(
            FakeIgdbRepository.PlatformsPageArgs("q", 0, 10),
            igdb.lastPlatformsPageArgs,
        )
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("PS5", result.getOrNull()?.first()?.name)
    }

    @Test
    fun getGenres_delegatesToRepository() = runTest {
        val igdb = FakeIgdbRepository().apply {
            genresResult = Result.success(listOf(Genre(10, "RPG")))
        }
        val useCase = GetGenresUseCase(igdb)
        val result = useCase()
        assertEquals("RPG", result.getOrNull()?.first()?.name)
    }

    @Test
    fun getGameDetails_delegatesToRepository() = runTest {
        val details = GameDetails(
            id = 99,
            name = "Test",
            summary = null,
            storyline = null,
            coverImageUrl = null,
            screenshotUrls = emptyList(),
            genreNames = emptyList(),
            firstReleaseDate = null,
        )
        val igdb = FakeIgdbRepository().apply { detailsResult = Result.success(details) }
        val useCase = GetGameDetailsUseCase(igdb)
        val result = useCase(99)
        assertEquals(99L, igdb.lastDetailsId)
        assertEquals("Test", result.getOrNull()?.name)
    }

    @Test
    fun observeCollection_emitsFilteredList() = runTest {
        val repo = FakeCollectionRepository().apply {
            setOwned(
                listOf(
                    OwnedGame(1, 10, "A", null, 0L),
                    OwnedGame(2, 20, "B", null, 0L),
                ),
            )
        }
        val useCase = ObserveCollectionUseCase(repo)
        useCase(10L).test {
            val first = awaitItem()
            assertEquals(1, first.size)
            assertEquals("A", first.single().title)
        }
    }

    @Test
    fun observeCollection_allPlatforms_whenFilterNull() = runTest {
        val repo = FakeCollectionRepository().apply {
            setOwned(listOf(OwnedGame(1, 10, "A", null, 0L)))
        }
        ObserveCollectionUseCase(repo)(null).test {
            assertEquals(1, awaitItem().size)
        }
    }

    @Test
    fun addGameToCollection_buildsOwnedGame() = runTest {
        val repo = FakeCollectionRepository()
        val useCase = AddGameToCollectionUseCase(repo)
        val details = GameDetails(
            id = 5,
            name = "Hollow",
            summary = null,
            storyline = null,
            coverImageUrl = "http://cover",
            screenshotUrls = emptyList(),
            genreNames = emptyList(),
            firstReleaseDate = null,
        )
        useCase(details, platformId = 7)
        assertTrue(repo.isInCollection(5, 7))
    }

    @Test
    fun removeFromCollection_delegates() = runTest {
        val repo = FakeCollectionRepository().apply {
            setOwned(listOf(OwnedGame(1, 2, "X", null, 0L)))
        }
        val useCase = RemoveFromCollectionUseCase(repo)
        useCase(1, 2)
        assertFalse(repo.isInCollection(1, 2))
    }

    @Test
    fun isGameInCollection_delegates() = runTest {
        val repo = FakeCollectionRepository().apply {
            setOwned(listOf(OwnedGame(1, 2, "X", null, 0L)))
        }
        val useCase = IsGameInCollectionUseCase(repo)
        assertTrue(useCase(1, 2))
        assertFalse(useCase(9, 2))
    }
}
