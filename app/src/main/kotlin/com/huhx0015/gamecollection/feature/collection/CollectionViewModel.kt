package com.huhx0015.gamecollection.feature.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huhx0015.gamecollection.SEARCH_DEBOUNCE_MS
import com.huhx0015.gamecollection.domain.model.OwnedGame
import com.huhx0015.gamecollection.domain.repository.IgdbRepository
import com.huhx0015.gamecollection.domain.usecase.ObserveCollectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

/** Local search and optional platform filter for the owned-games list. */
data class CollectionUiState(
    val searchQuery: String = "",
    val platformFilter: Long? = null,
)

/** Presents Room-backed owned games with search, platform filter, and chunked loading (10 per page). */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val observeCollectionUseCase: ObserveCollectionUseCase,
    private val igdbRepository: IgdbRepository,
) : ViewModel() {

    private companion object {
        const val PAGE_SIZE = 10
    }

    private val _ui = MutableStateFlow(CollectionUiState())
    val uiState: StateFlow<CollectionUiState> = _ui.asStateFlow()

    private val _visibleCount = MutableStateFlow(PAGE_SIZE)

    private val allOwnedGames: Flow<List<OwnedGame>> = observeCollectionUseCase(null)

    private val _platformNames = MutableStateFlow<Map<Long, String>>(emptyMap())
    /** IGDB display names for platforms present in the collection; falls back to numeric id if lookup fails. */
    val platformNames: StateFlow<Map<Long, String>> = _platformNames.asStateFlow()

    init {
        viewModelScope.launch {
            allOwnedGames
                .map { games -> games.map { it.platformId }.distinct().sorted() }
                .distinctUntilChanged()
                .collectLatest { ids ->
                    if (ids.isEmpty()) {
                        _platformNames.value = emptyMap()
                    } else {
                        igdbRepository.fetchPlatformsByIds(ids.toSet()).fold(
                            onSuccess = { list ->
                                val byId = list.associate { it.id to it.name }
                                _platformNames.value = ids.associateWith { id ->
                                    byId[id] ?: "ID $id"
                                }
                            },
                            onFailure = {
                                _platformNames.value = ids.associateWith { id -> "ID $id" }
                            },
                        )
                    }
                }
        }
    }

    val platformIdsInCollection: StateFlow<List<Long>> =
        allOwnedGames
            .map { games -> games.map { it.platformId }.distinct().sorted() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val filteredGames: StateFlow<List<OwnedGame>> = combine(
        _ui.map { it.platformFilter }.distinctUntilChanged(),
        _ui
            .map { it.searchQuery }
            .debounce { q -> if (q.isBlank()) 0L else SEARCH_DEBOUNCE_MS }
            .distinctUntilChanged(),
    ) { platformFilter, searchQuery ->
        platformFilter to searchQuery
    }
        .flatMapLatest { (platformFilter, searchQuery) ->
            observeCollectionUseCase(platformFilter).map { list ->
                val q = searchQuery.trim()
                if (q.isEmpty()) list else list.filter { it.title.contains(q, ignoreCase = true) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val displayedGames: StateFlow<List<OwnedGame>> =
        combine(filteredGames, _visibleCount) { games, n ->
            games.take(minOf(n, games.size))
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hasMoreGames: StateFlow<Boolean> =
        combine(filteredGames, _visibleCount) { games, n ->
            n < games.size
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun onSearchChange(query: String) {
        _ui.update { it.copy(searchQuery = query) }
        _visibleCount.value = PAGE_SIZE
    }

    fun onPlatformFilter(platformId: Long?) {
        _ui.update { it.copy(platformFilter = platformId) }
        _visibleCount.value = PAGE_SIZE
    }

    fun loadMore() {
        _visibleCount.update { it + PAGE_SIZE }
    }
}
