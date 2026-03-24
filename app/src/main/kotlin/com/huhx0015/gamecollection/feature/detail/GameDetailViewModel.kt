package com.huhx0015.gamecollection.feature.detail

import androidx.lifecycle.SavedStateHandle
import com.huhx0015.gamecollection.architecture.mvi.BaseViewModel
import com.huhx0015.gamecollection.domain.usecase.AddGameToCollectionUseCase
import com.huhx0015.gamecollection.domain.usecase.GetGameDetailsUseCase
import com.huhx0015.gamecollection.domain.usecase.IsGameInCollectionUseCase
import com.huhx0015.gamecollection.domain.usecase.RemoveFromCollectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/** Fetches IGDB details and handles add-to-collection for the current game/platform. */
@HiltViewModel
class GameDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getGameDetails: GetGameDetailsUseCase,
    private val addGame: AddGameToCollectionUseCase,
    private val removeGame: RemoveFromCollectionUseCase,
    private val isInCollection: IsGameInCollectionUseCase,
) : BaseViewModel<GameDetailState, GameDetailIntent, GameDetailEvent>() {

    val gameId: Long = checkNotNull(savedStateHandle["gameId"])
    val platformId: Long = checkNotNull(savedStateHandle["platformId"])

    private val _state = MutableStateFlow(GameDetailState())
    override val state: StateFlow<GameDetailState> = _state.asStateFlow()

    override val events: Flow<GameDetailEvent> = emptyFlow()

    init {
        sendIntent(GameDetailIntent.Refresh)
    }

    override suspend fun processIntent(intent: GameDetailIntent) {
        when (intent) {
            GameDetailIntent.Refresh -> refresh()
            GameDetailIntent.AddToCollection -> addToCollection()
            GameDetailIntent.RemoveFromCollection -> removeFromCollection()
        }
    }

    private suspend fun refresh() {
        _state.update { it.copy(isLoading = true, error = null) }
        getGameDetails(gameId)
            .onSuccess { details ->
                val owned = isInCollection(gameId, platformId)
                _state.update {
                    it.copy(
                        isLoading = false,
                        details = details,
                        inCollection = owned,
                        error = null,
                    )
                }
            }
            .onFailure { e ->
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Error", details = null)
                }
            }
    }

    private suspend fun addToCollection() {
        val details = _state.value.details ?: return
        _state.update { it.copy(addInProgress = true, addMessage = null) }
        addGame(details, platformId)
            .onSuccess {
                _state.update {
                    it.copy(addInProgress = false, inCollection = true, addMessage = "Added")
                }
            }
            .onFailure { e ->
                _state.update {
                    it.copy(addInProgress = false, addMessage = e.message ?: "Failed")
                }
            }
    }

    private suspend fun removeFromCollection() {
        _state.update { it.copy(removeInProgress = true, addMessage = null) }
        removeGame(gameId, platformId)
            .onSuccess {
                _state.update {
                    it.copy(removeInProgress = false, inCollection = false, addMessage = "Removed")
                }
            }
            .onFailure { e ->
                _state.update {
                    it.copy(removeInProgress = false, addMessage = e.message ?: "Failed")
                }
            }
    }
}
