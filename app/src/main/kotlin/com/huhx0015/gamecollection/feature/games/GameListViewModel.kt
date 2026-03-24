package com.huhx0015.gamecollection.feature.games

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.huhx0015.gamecollection.SEARCH_DEBOUNCE_MS
import com.huhx0015.gamecollection.architecture.mvi.BaseViewModel
import com.huhx0015.gamecollection.data.paging.IgdbGamesPagingSource
import com.huhx0015.gamecollection.domain.repository.IgdbRepository
import com.huhx0015.gamecollection.domain.usecase.GetGenresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Paged catalog for a chosen platform with genre list and filter state. */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class GameListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val igdbRepository: IgdbRepository,
    private val getGenresUseCase: GetGenresUseCase,
) : BaseViewModel<GameListState, GameListIntent, GameListEvent>() {

    val platformId: Long = checkNotNull(savedStateHandle["platformId"])

    private val _state = MutableStateFlow(GameListState())
    override val state: StateFlow<GameListState> = _state.asStateFlow()

    override val events: Flow<GameListEvent> = emptyFlow()

    val pagingFlow = combine(
        state.map { it.filters.selectedGenreIds }.distinctUntilChanged(),
        state
            .map { it.filters.searchQuery }
            .debounce { q -> if (q.isBlank()) 0L else SEARCH_DEBOUNCE_MS }
            .distinctUntilChanged(),
    ) { genreIds, searchQuery ->
        GameListFilters(searchQuery = searchQuery, selectedGenreIds = genreIds)
    }
        .distinctUntilChanged()
        .flatMapLatest { f ->
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false,
                    initialLoadSize = 10,
                ),
                pagingSourceFactory = {
                    IgdbGamesPagingSource(
                        platformId = platformId,
                        searchQuery = f.searchQuery.takeIf { it.isNotBlank() },
                        genreIds = f.selectedGenreIds,
                        igdbRepository = igdbRepository,
                    )
                },
            ).flow
        }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            getGenresUseCase()
                .onSuccess { genres -> _state.update { it.copy(allGenres = genres) } }
                .onFailure { e -> _state.update { it.copy(genreError = e.message) } }
        }
    }

    override suspend fun processIntent(intent: GameListIntent) {
        when (intent) {
            is GameListIntent.SearchChanged -> {
                _state.update { it.copy(filters = it.filters.copy(searchQuery = intent.query)) }
            }
            is GameListIntent.ToggleGenre -> {
                _state.update { s ->
                    val f = s.filters
                    val next = f.selectedGenreIds.toMutableSet()
                    if (!next.add(intent.genreId)) next.remove(intent.genreId)
                    s.copy(filters = f.copy(selectedGenreIds = next))
                }
            }
            GameListIntent.ClearGenres -> {
                _state.update { it.copy(filters = it.filters.copy(selectedGenreIds = emptySet())) }
            }
        }
    }
}
