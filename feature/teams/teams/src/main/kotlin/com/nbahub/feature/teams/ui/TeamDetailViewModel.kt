package com.nbahub.feature.teams.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nbahub.feature.teams.data.TeamsService
import com.nbahub.feature.teams.data.model.Player
import com.nbahub.feature.teams.data.model.Team
import com.nbahub.feature.teams.TeamsFeatureDependencies
import com.nbahub.platform.storage.StorageClient
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal sealed interface TeamDetailUiState {
    data object Loading : TeamDetailUiState
    data class Success(
        val team: Team,
        val players: List<Player>,
        val isFavorite: Boolean,
    ) : TeamDetailUiState
    data class Error(val message: String) : TeamDetailUiState
}

internal class TeamDetailViewModel(
    private val teamId: Int,
    private val teamsService: TeamsService,
    private val storageClient: StorageClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TeamDetailUiState>(TeamDetailUiState.Loading)
    val uiState: StateFlow<TeamDetailUiState> = _uiState.asStateFlow()

    init {
        loadTeamDetail()
        observeFavorite()
    }

    private fun loadTeamDetail() {
        viewModelScope.launch {
            try {
                coroutineScope {
                    val teamDeferred = async { teamsService.getTeam(teamId) }
                    val playersDeferred = async { teamsService.getPlayersByTeam(teamId) }
                    val team = teamDeferred.await()
                    val players = playersDeferred.await()
                    val isFavorite = storageClient.isFavoriteTeam(teamId)
                    _uiState.value = TeamDetailUiState.Success(team, players, isFavorite)
                }
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _uiState.value = TeamDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun observeFavorite() {
        viewModelScope.launch {
            storageClient.observeFavoriteTeamIds().collect { favoriteIds ->
                _uiState.update { current ->
                    if (current is TeamDetailUiState.Success) {
                        current.copy(isFavorite = teamId in favoriteIds)
                    } else {
                        current
                    }
                }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            storageClient.toggleFavoriteTeam(teamId)
        }
    }

    companion object {
        fun factory(teamId: Int, deps: TeamsFeatureDependencies) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    TeamDetailViewModel(teamId, TeamsService(deps.networkClient), deps.storageClient) as T
            }
    }
}
