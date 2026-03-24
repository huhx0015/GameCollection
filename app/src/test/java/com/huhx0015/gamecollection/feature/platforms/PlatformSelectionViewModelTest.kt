package com.huhx0015.gamecollection.feature.platforms

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import com.huhx0015.gamecollection.MainDispatcherRule
import com.huhx0015.gamecollection.SEARCH_DEBOUNCE_MS
import com.huhx0015.gamecollection.domain.model.GamePlatform
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

/** Tests [PlatformSelectionViewModel] platform paging and navigation events. */
@OptIn(ExperimentalCoroutinesApi::class)
class PlatformSelectionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun load_populatesPlatforms() = runTest(testDispatcher) {
        val fake = FakeIgdbRepository().apply {
            platformsResult = Result.success(
                listOf(GamePlatform(1, "PS5", "ps5", null)),
            )
        }
        val vm = PlatformSelectionViewModel(fake)
        advanceUntilIdle()
        val snapshot = vm.platformPaging.asSnapshot()
        assertEquals(1, snapshot.size)
        assertEquals("PS5", snapshot.first().name)
    }

    @Test
    fun platformClicked_emitsNavigationEvent() = runTest(testDispatcher) {
        val fake = FakeIgdbRepository().apply {
            platformsResult = Result.success(emptyList())
        }
        val vm = PlatformSelectionViewModel(fake)
        vm.events.test {
            vm.sendIntent(PlatformSelectionIntent.PlatformClicked(42L))
            advanceUntilIdle()
            assertEquals(
                PlatformSelectionEvent.NavigateToGameList(42L),
                awaitItem(),
            )
        }
    }

    @Test
    fun searchChanged_rapidTypingDebouncesPlatformFetch() = runTest(testDispatcher) {
        val fake = FakeIgdbRepository().apply {
            platformsResult = Result.success(
                listOf(GamePlatform(1, "PS5", "ps5", null)),
            )
        }
        val vm = PlatformSelectionViewModel(fake)
        advanceUntilIdle()
        fake.fetchPlatformsPageQueries.clear()
        vm.sendIntent(PlatformSelectionIntent.SearchChanged("p"))
        vm.sendIntent(PlatformSelectionIntent.SearchChanged("ps"))
        vm.sendIntent(PlatformSelectionIntent.SearchChanged("ps5"))
        advanceTimeBy(SEARCH_DEBOUNCE_MS)
        advanceUntilIdle()
        vm.platformPaging.asSnapshot()
        assertTrue(fake.fetchPlatformsPageQueries.any { it == "ps5" })
        assertTrue(fake.fetchPlatformsPageQueries.none { it == "p" || it == "ps" })
    }
}
