package com.huhx0015.gamecollection.feature.platforms

import app.cash.turbine.test
import com.huhx0015.gamecollection.MainDispatcherRule
import com.huhx0015.gamecollection.domain.model.GamePlatform
import com.huhx0015.gamecollection.domain.usecase.GetPlatformsUseCase
import com.huhx0015.gamecollection.fakes.FakeIgdbRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

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
        val vm = PlatformSelectionViewModel(GetPlatformsUseCase(fake))
        advanceUntilIdle()
        assertEquals(1, vm.state.value.platforms.size)
        assertEquals("PS5", vm.state.value.platforms.first().name)
        assertEquals(false, vm.state.value.isLoading)
    }

    @Test
    fun platformClicked_emitsNavigationEvent() = runTest {
        val fake = FakeIgdbRepository().apply {
            platformsResult = Result.success(emptyList())
        }
        val vm = PlatformSelectionViewModel(GetPlatformsUseCase(fake))
        advanceUntilIdle()
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
