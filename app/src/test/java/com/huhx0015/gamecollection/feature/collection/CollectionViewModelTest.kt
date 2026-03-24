package com.huhx0015.gamecollection.feature.collection

import com.huhx0015.gamecollection.MainDispatcherRule
import com.huhx0015.gamecollection.SEARCH_DEBOUNCE_MS
import com.huhx0015.gamecollection.domain.model.OwnedGame
import com.huhx0015.gamecollection.domain.usecase.ObserveCollectionUseCase
import com.huhx0015.gamecollection.fakes.FakeCollectionRepository
import com.huhx0015.gamecollection.fakes.FakeIgdbRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Tests [CollectionViewModel] debounced local search and platform filter. */
@OptIn(ExperimentalCoroutinesApi::class)
class CollectionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun onSearchChange_updatesUiStateImmediately() = runTest(testDispatcher) {
        val repo = FakeCollectionRepository().apply {
            setOwned(
                listOf(
                    OwnedGame(1, 1, "aaa", null, 0L),
                    OwnedGame(2, 1, "bbb", null, 0L),
                ),
            )
        }
        val vm = CollectionViewModel(ObserveCollectionUseCase(repo), FakeIgdbRepository())
        backgroundScope.launch { vm.displayedGames.collect { } }
        advanceUntilIdle()
        vm.sendIntent(CollectionIntent.SearchChanged("findme"))
        advanceUntilIdle()
        assertEquals("findme", vm.state.value.searchQuery)
    }

    @Test
    fun rapidSearch_debouncesFiltering() = runTest(testDispatcher) {
        val repo = FakeCollectionRepository().apply {
            setOwned(
                listOf(
                    OwnedGame(1, 1, "aaa", null, 0L),
                    OwnedGame(2, 1, "bbb", null, 0L),
                ),
            )
        }
        val vm = CollectionViewModel(ObserveCollectionUseCase(repo), FakeIgdbRepository())
        backgroundScope.launch { vm.displayedGames.collect { } }
        advanceUntilIdle()
        assertEquals(2, vm.displayedGames.value.size)
        vm.sendIntent(CollectionIntent.SearchChanged("b"))
        vm.sendIntent(CollectionIntent.SearchChanged("bb"))
        vm.sendIntent(CollectionIntent.SearchChanged("bbb"))
        runCurrent()
        assertEquals("bbb", vm.state.value.searchQuery)
        assertEquals(2, vm.displayedGames.value.size)
        advanceTimeBy(SEARCH_DEBOUNCE_MS)
        advanceUntilIdle()
        assertEquals(1, vm.displayedGames.value.size)
        assertEquals("bbb", vm.displayedGames.value.first().title)
    }
}
