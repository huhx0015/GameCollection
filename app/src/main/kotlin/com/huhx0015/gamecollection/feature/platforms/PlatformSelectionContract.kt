package com.huhx0015.gamecollection.feature.platforms

import com.huhx0015.gamecollection.architecture.mvi.BaseEvent
import com.huhx0015.gamecollection.architecture.mvi.BaseIntent
import com.huhx0015.gamecollection.architecture.mvi.BaseState

/** UI state for the platform picker (search field only; list uses paging). */
data class PlatformSelectionState(
    val searchQuery: String = "",
) : BaseState

/** User actions on the platform selection screen. */
sealed interface PlatformSelectionIntent : BaseIntent {
    data class SearchChanged(val query: String) : PlatformSelectionIntent
    data class PlatformClicked(val platformId: Long) : PlatformSelectionIntent
}

/** Side effects emitted after a platform is chosen (e.g. navigate to game list). */
sealed interface PlatformSelectionEvent : BaseEvent {
    data class NavigateToGameList(val platformId: Long) : PlatformSelectionEvent
}
