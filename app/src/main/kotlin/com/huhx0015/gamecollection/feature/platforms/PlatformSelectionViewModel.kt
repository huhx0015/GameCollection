package com.huhx0015.gamecollection.feature.platforms

import androidx.lifecycle.viewModelScope
import com.huhx0015.gamecollection.architecture.mvi.BaseViewModel
import com.huhx0015.gamecollection.domain.usecase.GetPlatformsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlatformSelectionViewModel @Inject constructor(
    private val getPlatformsUseCase: GetPlatformsUseCase,
) : BaseViewModel<PlatformSelectionState, PlatformSelectionIntent, PlatformSelectionEvent>() {

    private val _state = MutableStateFlow(PlatformSelectionState())
    override val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<PlatformSelectionEvent>()
    override val events: Flow<PlatformSelectionEvent> = _events.asSharedFlow()

    init {
        sendIntent(PlatformSelectionIntent.Load)
    }

    override suspend fun processIntent(intent: PlatformSelectionIntent) {
        when (intent) {
            PlatformSelectionIntent.Load -> load()
            is PlatformSelectionIntent.SearchChanged -> {
                _state.update { it.copy(searchQuery = intent.query) }
                load()
            }
            is PlatformSelectionIntent.PlatformClicked -> {
                _events.emit(PlatformSelectionEvent.NavigateToGameList(intent.platformId))
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val q = _state.value.searchQuery.trim().takeIf { it.isNotEmpty() }
            getPlatformsUseCase(q)
                .onSuccess { list ->
                    _state.update {
                        it.copy(platforms = list, isLoading = false, errorMessage = null)
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Error")
                    }
                }
        }
    }
}
