package com.huhx0015.gamecollection.feature.platforms

import com.huhx0015.gamecollection.architecture.mvi.BaseEvent
import com.huhx0015.gamecollection.architecture.mvi.BaseIntent
import com.huhx0015.gamecollection.architecture.mvi.BaseState
import com.huhx0015.gamecollection.domain.model.GamePlatform

data class PlatformSelectionState(
    val searchQuery: String = "",
    val platforms: List<GamePlatform> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : BaseState

sealed interface PlatformSelectionIntent : BaseIntent {
    data object Load : PlatformSelectionIntent
    data class SearchChanged(val query: String) : PlatformSelectionIntent
    data class PlatformClicked(val platformId: Long) : PlatformSelectionIntent
}

sealed interface PlatformSelectionEvent : BaseEvent {
    data class NavigateToGameList(val platformId: Long) : PlatformSelectionEvent
}
