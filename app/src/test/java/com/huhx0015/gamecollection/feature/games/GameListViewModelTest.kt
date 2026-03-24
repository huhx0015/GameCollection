package com.huhx0015.gamecollection.feature.games

import androidx.lifecycle.SavedStateHandle
import androidx.paging.testing.asSnapshot
import com.huhx0015.gamecollection.MainDispatcherRule
import com.huhx0015.gamecollection.SEARCH_DEBOUNCE_MS
import com.huhx0015.gamecollection.domain.model.GameSummary
import com.huhx0015.gamecollection.domain.usecase.GetGenresUseCase
import com.huhx0015.gamecollection.fakes.FakeIgdbRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Tests [GameListViewModel] filters and debounced IGDB game paging. */
@OptIn(ExperimentalCoroutinesApi::class)
class GameListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun onSearchChange_updatesFiltersImmediately() = runTest(testDispatcher) {
        val fake = FakeIgdbRepository().apply {
            genresResult = Result.success(emptyList())
            gamesPageResult = Result.success(emptyList())
        }
        val vm = GameListViewModel(
            SavedStateHandle(mapOf("platformId" to 1L)),
            fake,
            GetGenresUseCase(fake),
        )
        advanceUntilIdle()
        vm.onSearchChange("zelda")
        assertEquals("zelda", vm.filters.value.searchQuery)
    }

    @Test
    fun rapidSearch_debouncesRemoteGameFetch() = runTest(testDispatcher) {
        val fake = FakeIgdbRepository().apply {
            genresResult = Result.success(emptyList())
            gamesPageResult = Result.success(
                listOf(GameSummary(1, "Zelda", null, null, emptyList())),
            )
        }
        val vm = GameListViewModel(
            SavedStateHandle(mapOf("platformId" to 1L)),
            fake,
            GetGenresUseCase(fake),
        )
        advanceUntilIdle()
        fake.fetchGamesPageSearchQueries.clear()
        vm.onSearchChange("z")
        vm.onSearchChange("ze")
        vm.onSearchChange("zel")
        assertEquals("zel", vm.filters.value.searchQuery)
        advanceTimeBy(SEARCH_DEBOUNCE_MS)
        advanceUntilIdle()
        vm.pagingFlow.asSnapshot()
        assertTrue(
            fake.fetchGamesPageSearchQueries.any { it == "zel" },
        )
        assertTrue(
            fake.fetchGamesPageSearchQueries.none { it == "z" || it == "ze" },
        )
    }
}
