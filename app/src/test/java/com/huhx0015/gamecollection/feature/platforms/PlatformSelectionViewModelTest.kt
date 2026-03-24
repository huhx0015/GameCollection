package com.huhx0015.gamecollection.feature.platforms

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import com.huhx0015.gamecollection.MainDispatcherRule
import com.huhx0015.gamecollection.domain.model.GamePlatform
import com.huhx0015.gamecollection.fakes.FakeIgdbRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Tests [PlatformSelectionViewModel] platform paging and navigation events. */
@OptIn(ExperimentalCoroutinesApi::class)
class PlatformSelectionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun load_populatesPlatforms() = runTest {
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
    fun platformClicked_emitsNavigationEvent() = runTest {
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
}
