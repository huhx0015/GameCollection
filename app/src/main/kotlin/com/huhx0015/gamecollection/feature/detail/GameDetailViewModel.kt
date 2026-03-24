package com.huhx0015.gamecollection.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huhx0015.gamecollection.domain.model.GameDetails
import com.huhx0015.gamecollection.domain.usecase.AddGameToCollectionUseCase
import com.huhx0015.gamecollection.domain.usecase.GetGameDetailsUseCase
import com.huhx0015.gamecollection.domain.usecase.IsGameInCollectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Loading, content, errors, and collection actions for the game detail screen. */
data class GameDetailUiState(
    val isLoading: Boolean = true,
    val details: GameDetails? = null,
    val error: String? = null,
    val inCollection: Boolean = false,
    val addInProgress: Boolean = false,
    val addMessage: String? = null,
)

/** Fetches IGDB details and handles add-to-collection for the current game/platform. */
@HiltViewModel
class GameDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getGameDetails: GetGameDetailsUseCase,
    private val addGame: AddGameToCollectionUseCase,
    private val isInCollection: IsGameInCollectionUseCase,
) : ViewModel() {

    val gameId: Long = checkNotNull(savedStateHandle["gameId"])
    val platformId: Long = checkNotNull(savedStateHandle["platformId"])

    private val _ui = MutableStateFlow(GameDetailUiState())
    val uiState: StateFlow<GameDetailUiState> = _ui.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            getGameDetails(gameId)
                .onSuccess { details ->
                    val owned = isInCollection(gameId, platformId)
                    _ui.update {
                        it.copy(
                            isLoading = false,
                            details = details,
                            inCollection = owned,
                            error = null,
                        )
                    }
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(isLoading = false, error = e.message ?: "Error", details = null)
                    }
                }
        }
    }

    fun addToCollection() {
        val details = _ui.value.details ?: return
        viewModelScope.launch {
            _ui.update { it.copy(addInProgress = true, addMessage = null) }
            addGame(details, platformId)
                .onSuccess {
                    _ui.update {
                        it.copy(addInProgress = false, inCollection = true, addMessage = "Added")
                    }
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(addInProgress = false, addMessage = e.message ?: "Failed")
                    }
                }
        }
    }
}
