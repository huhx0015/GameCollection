package com.huhx0015.gamecollection.feature.platforms

import com.huhx0015.gamecollection.architecture.mvi.BaseEvent
import com.huhx0015.gamecollection.architecture.mvi.BaseIntent
import com.huhx0015.gamecollection.architecture.mvi.BaseState
import com.huhx0015.gamecollection.domain.model.GamePlatform

/** UI state for the platform picker (search, list, loading, errors). */
data class PlatformSelectionState(
    val searchQuery: String = "",
    val platforms: List<GamePlatform> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : BaseState

/** User actions on the platform selection screen. */
sealed interface PlatformSelectionIntent : BaseIntent {
    data object Load : PlatformSelectionIntent
    data class SearchChanged(val query: String) : PlatformSelectionIntent
    data class PlatformClicked(val platformId: Long) : PlatformSelectionIntent
}

/** Side effects emitted after a platform is chosen (e.g. navigate to game list). */
sealed interface PlatformSelectionEvent : BaseEvent {
    data class NavigateToGameList(val platformId: Long) : PlatformSelectionEvent
}
