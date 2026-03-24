package com.huhx0015.gamecollection.feature.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huhx0015.gamecollection.domain.model.OwnedGame
import com.huhx0015.gamecollection.domain.usecase.ObserveCollectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/** Local search and optional platform filter for the owned-games list. */
data class CollectionUiState(
    val searchQuery: String = "",
    val platformFilter: Long? = null,
)

/** Presents Room-backed owned games with search and platform filtering. */
@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val observeCollectionUseCase: ObserveCollectionUseCase,
) : ViewModel() {

    private val _ui = MutableStateFlow(CollectionUiState())
    val uiState: StateFlow<CollectionUiState> = _ui.asStateFlow()

    val platformIdsInCollection: StateFlow<List<Long>> =
        observeCollectionUseCase(null)
            .map { games -> games.map { it.platformId }.distinct().sorted() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ownedGames: StateFlow<List<OwnedGame>> = _ui
        .flatMapLatest { s ->
            observeCollectionUseCase(s.platformFilter).map { list ->
                val q = s.searchQuery.trim()
                if (q.isEmpty()) list
                else list.filter { it.title.contains(q, ignoreCase = true) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchChange(query: String) {
        _ui.update { it.copy(searchQuery = query) }
    }

    fun onPlatformFilter(platformId: Long?) {
        _ui.update { it.copy(platformFilter = platformId) }
    }
}
