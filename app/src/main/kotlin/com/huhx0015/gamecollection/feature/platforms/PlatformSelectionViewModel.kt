package com.huhx0015.gamecollection.feature.platforms

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.huhx0015.gamecollection.architecture.mvi.BaseViewModel
import com.huhx0015.gamecollection.data.paging.IgdbPlatformsPagingSource
import com.huhx0015.gamecollection.domain.model.GamePlatform
import com.huhx0015.gamecollection.domain.repository.IgdbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi

private const val PLATFORM_PAGE_SIZE = 10

/** Loads and searches IGDB platforms with paging; emits navigation when one is selected. */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlatformSelectionViewModel @Inject constructor(
    private val igdbRepository: IgdbRepository,
) : BaseViewModel<PlatformSelectionState, PlatformSelectionIntent, PlatformSelectionEvent>() {

    private val _state = MutableStateFlow(PlatformSelectionState())
    override val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<PlatformSelectionEvent>()
    override val events: Flow<PlatformSelectionEvent> = _events.asSharedFlow()

    val platformPaging: Flow<PagingData<GamePlatform>> = _state
        .map { it.searchQuery.trim().takeIf { q -> q.isNotEmpty() } }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(
                    pageSize = PLATFORM_PAGE_SIZE,
                    enablePlaceholders = false,
                    initialLoadSize = PLATFORM_PAGE_SIZE,
                ),
                pagingSourceFactory = {
                    IgdbPlatformsPagingSource(
                        searchQuery = query,
                        igdbRepository = igdbRepository,
                    )
                },
            ).flow
        }
        .cachedIn(viewModelScope)

    override suspend fun processIntent(intent: PlatformSelectionIntent) {
        when (intent) {
            is PlatformSelectionIntent.SearchChanged -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }
            is PlatformSelectionIntent.PlatformClicked -> {
                _events.emit(PlatformSelectionEvent.NavigateToGameList(intent.platformId))
            }
        }
    }
}
