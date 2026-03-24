package com.huhx0015.gamecollection.feature.detail

import com.huhx0015.gamecollection.architecture.mvi.BaseEvent
import com.huhx0015.gamecollection.architecture.mvi.BaseIntent
import com.huhx0015.gamecollection.architecture.mvi.BaseState
import com.huhx0015.gamecollection.domain.model.GameDetails

/** Loading, content, errors, and collection actions for the game detail screen. */
data class GameDetailState(
    val isLoading: Boolean = true,
    val details: GameDetails? = null,
    val error: String? = null,
    val inCollection: Boolean = false,
    val addInProgress: Boolean = false,
    val addMessage: String? = null,
) : BaseState

sealed interface GameDetailIntent : BaseIntent {
    data object Refresh : GameDetailIntent
    data object AddToCollection : GameDetailIntent
}

/** Side effects for game detail (none yet). */
sealed interface GameDetailEvent : BaseEvent
