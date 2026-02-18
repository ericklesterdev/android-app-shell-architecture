package com.nbahub.feature.scores.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbahub.feature.scores.data.ScoresService
import com.nbahub.feature.scores.data.model.Game
import com.nbahub.platform.storage.StorageClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal data class ScoresListUiState(
    val games: List<Game> = emptyList(),
    val favoriteTeamIds: Set<Int> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

internal class ScoresListViewModel @Inject constructor(
    private val scoresService: ScoresService,
    private val storageClient: StorageClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScoresListUiState())
    val uiState: StateFlow<ScoresListUiState> = _uiState.asStateFlow()

    init {
        loadGames()
        observeFavorites()
    }

    private fun loadGames() {
        viewModelScope.launch {
            try {
                val games = scoresService.getGames()
                _uiState.update { it.copy(games = games, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            storageClient.observeFavoriteTeamIds().collect { favoriteIds ->
                _uiState.update { it.copy(favoriteTeamIds = favoriteIds) }
            }
        }
    }
}
