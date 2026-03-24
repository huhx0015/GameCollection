package com.huhx0015.gamecollection.feature.collection

import com.huhx0015.gamecollection.architecture.mvi.BaseEvent
import com.huhx0015.gamecollection.architecture.mvi.BaseIntent
import com.huhx0015.gamecollection.architecture.mvi.BaseState

/** Local search and optional platform filter for the owned-games list. */
data class CollectionState(
    val searchQuery: String = "",
    val platformFilter: Long? = null,
) : BaseState

sealed interface CollectionIntent : BaseIntent {
    data class SearchChanged(val query: String) : CollectionIntent
    data class PlatformFilterChanged(val platformId: Long?) : CollectionIntent
    data object LoadMore : CollectionIntent
}

/** Side effects for the collection screen (none yet). */
sealed interface CollectionEvent : BaseEvent
