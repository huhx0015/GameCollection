package com.huhx0015.gamecollection.feature.detail

import androidx.lifecycle.SavedStateHandle
import com.huhx0015.gamecollection.MainDispatcherRule
import com.huhx0015.gamecollection.domain.model.GameDetails
import com.huhx0015.gamecollection.domain.model.OwnedGame
import com.huhx0015.gamecollection.domain.usecase.AddGameToCollectionUseCase
import com.huhx0015.gamecollection.domain.usecase.GetGameDetailsUseCase
import com.huhx0015.gamecollection.domain.usecase.IsGameInCollectionUseCase
import com.huhx0015.gamecollection.domain.usecase.RemoveFromCollectionUseCase
import com.huhx0015.gamecollection.fakes.FakeCollectionRepository
import com.huhx0015.gamecollection.fakes.FakeIgdbRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Tests [GameDetailViewModel] detail loading, collection membership, and add flow. */
@OptIn(ExperimentalCoroutinesApi::class)
class GameDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun refresh_loadsDetailsAndCollectionFlag() = runTest {
        val details = GameDetails(
            id = 1,
            name = "Test Game",
            summary = null,
            storyline = null,
            coverImageUrl = null,
            screenshotUrls = emptyList(),
            genreNames = listOf("Action"),
            firstReleaseDate = null,
        )
        val igdb = FakeIgdbRepository().apply { detailsResult = Result.success(details) }
        val collection = FakeCollectionRepository().apply {
            setOwned(emptyList())
        }
        val handle = SavedStateHandle().apply {
            set("gameId", 1L)
            set("platformId", 2L)
        }
        val vm = GameDetailViewModel(
            handle,
            GetGameDetailsUseCase(igdb),
            AddGameToCollectionUseCase(collection),
            RemoveFromCollectionUseCase(collection),
            IsGameInCollectionUseCase(collection),
        )
        advanceUntilIdle()
        assertEquals("Test Game", vm.state.value.details?.name)
        assertFalse(vm.state.value.inCollection)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun addToCollection_updatesState() = runTest {
        val details = GameDetails(
            id = 1,
            name = "Test Game",
            summary = null,
            storyline = null,
            coverImageUrl = null,
            screenshotUrls = emptyList(),
            genreNames = emptyList(),
            firstReleaseDate = null,
        )
        val igdb = FakeIgdbRepository().apply { detailsResult = Result.success(details) }
        val collection = FakeCollectionRepository()
        val handle = SavedStateHandle().apply {
            set("gameId", 1L)
            set("platformId", 2L)
        }
        val vm = GameDetailViewModel(
            handle,
            GetGameDetailsUseCase(igdb),
            AddGameToCollectionUseCase(collection),
            RemoveFromCollectionUseCase(collection),
            IsGameInCollectionUseCase(collection),
        )
        advanceUntilIdle()
        vm.sendIntent(GameDetailIntent.AddToCollection)
        advanceUntilIdle()
        assertTrue(vm.state.value.inCollection)
        assertEquals("Added", vm.state.value.addMessage)
    }

    @Test
    fun removeFromCollection_updatesState() = runTest {
        val details = GameDetails(
            id = 1,
            name = "Test Game",
            summary = null,
            storyline = null,
            coverImageUrl = null,
            screenshotUrls = emptyList(),
            genreNames = emptyList(),
            firstReleaseDate = null,
        )
        val igdb = FakeIgdbRepository().apply { detailsResult = Result.success(details) }
        val collection = FakeCollectionRepository().apply {
            setOwned(
                listOf(
                    OwnedGame(
                        igdbGameId = 1,
                        platformId = 2,
                        title = "Test Game",
                        coverUrl = null,
                        addedAtEpochMillis = 0L,
                    ),
                ),
            )
        }
        val handle = SavedStateHandle().apply {
            set("gameId", 1L)
            set("platformId", 2L)
        }
        val vm = GameDetailViewModel(
            handle,
            GetGameDetailsUseCase(igdb),
            AddGameToCollectionUseCase(collection),
            RemoveFromCollectionUseCase(collection),
            IsGameInCollectionUseCase(collection),
        )
        advanceUntilIdle()
        assertTrue(vm.state.value.inCollection)
        vm.sendIntent(GameDetailIntent.RemoveFromCollection)
        advanceUntilIdle()
        assertFalse(vm.state.value.inCollection)
        assertEquals("Removed", vm.state.value.addMessage)
    }
}
