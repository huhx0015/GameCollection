package com.huhx0015.gamecollection.feature.games

import com.huhx0015.gamecollection.architecture.mvi.BaseEvent
import com.huhx0015.gamecollection.architecture.mvi.BaseIntent
import com.huhx0015.gamecollection.architecture.mvi.BaseState
import com.huhx0015.gamecollection.domain.model.Genre

/** Search text and genre filters applied to the paged IGDB game list. */
data class GameListFilters(
    val searchQuery: String = "",
    val selectedGenreIds: Set<Long> = emptySet(),
)

/** Paged catalog filters, genre list, and genre load errors for the game list screen. */
data class GameListState(
    val filters: GameListFilters = GameListFilters(),
    val allGenres: List<Genre> = emptyList(),
    val genreError: String? = null,
) : BaseState

sealed interface GameListIntent : BaseIntent {
    data class SearchChanged(val query: String) : GameListIntent
    data class ToggleGenre(val genreId: Long) : GameListIntent
    data object ClearGenres : GameListIntent
}

/** Side effects for the game list (none yet; reserved for navigation/snackbar). */
sealed interface GameListEvent : BaseEvent
