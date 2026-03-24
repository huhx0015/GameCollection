package com.huhx0015.gamecollection.feature.games

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.huhx0015.gamecollection.SEARCH_DEBOUNCE_MS
import com.huhx0015.gamecollection.data.paging.IgdbGamesPagingSource
import com.huhx0015.gamecollection.domain.repository.IgdbRepository
import com.huhx0015.gamecollection.domain.usecase.GetGenresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

/** Search text and genre filters applied to the paged IGDB game list. */
data class GameListFilters(
    val searchQuery: String = "",
    val selectedGenreIds: Set<Long> = emptySet(),
)

/** Paged catalog for a chosen platform with genre list and filter state. */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class GameListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val igdbRepository: IgdbRepository,
    private val getGenresUseCase: GetGenresUseCase,
) : ViewModel() {

    val platformId: Long = checkNotNull(savedStateHandle["platformId"])

    private val _filters = MutableStateFlow(GameListFilters())
    val filters: StateFlow<GameListFilters> = _filters.asStateFlow()

    private val _allGenres = MutableStateFlow<List<com.huhx0015.gamecollection.domain.model.Genre>>(emptyList())
    val allGenres = _allGenres.asStateFlow()

    private val _genreError = MutableStateFlow<String?>(null)
    val genreError = _genreError.asStateFlow()

    val pagingFlow = combine(
        _filters.map { it.selectedGenreIds }.distinctUntilChanged(),
        _filters
            .map { it.searchQuery }
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
                .onSuccess { _allGenres.value = it }
                .onFailure { e -> _genreError.value = e.message }
        }
    }

    fun onSearchChange(query: String) {
        _filters.update { it.copy(searchQuery = query) }
    }

    fun toggleGenre(genreId: Long) {
        _filters.update { f ->
            val next = f.selectedGenreIds.toMutableSet()
            if (!next.add(genreId)) next.remove(genreId)
            f.copy(selectedGenreIds = next)
        }
    }

    fun clearGenres() {
        _filters.update { it.copy(selectedGenreIds = emptySet()) }
    }
}
