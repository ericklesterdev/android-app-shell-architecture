package com.nbahub.feature.teams.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nbahub.feature.teams.R
import com.nbahub.feature.teams.data.TeamsService
import com.nbahub.feature.teams.data.model.Team
import com.nbahub.feature.teams.TeamsFeatureDependencies
import com.nbahub.platform.storage.StorageClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal enum class Conference(@StringRes val labelRes: Int) {
    ALL(R.string.teams_filter_all),
    EASTERN(R.string.teams_filter_eastern),
    WESTERN(R.string.teams_filter_western),
}

internal data class TeamsListUiState(
    val teams: List<Team> = emptyList(),
    val favoriteTeamIds: Set<Int> = emptySet(),
    val selectedConference: Conference = Conference.ALL,
    val isLoading: Boolean = true,
    val error: String? = null,
)

internal class TeamsListViewModel(
    private val teamsService: TeamsService,
    private val storageClient: StorageClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeamsListUiState())
    val uiState: StateFlow<TeamsListUiState> = _uiState.asStateFlow()

    init {
        loadTeams()
        observeFavorites()
    }

    private fun loadTeams() {
        viewModelScope.launch {
            try {
                val teams = teamsService.getTeams()
                _uiState.update { it.copy(teams = teams, isLoading = false) }
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun retry() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        loadTeams()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            storageClient.observeFavoriteTeamIds().collect { favoriteIds ->
                _uiState.update { it.copy(favoriteTeamIds = favoriteIds) }
            }
        }
    }

    fun selectConference(conference: Conference) {
        _uiState.update { it.copy(selectedConference = conference) }
    }

    companion object {
        fun factory(deps: TeamsFeatureDependencies) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    TeamsListViewModel(TeamsService(deps.networkClient), deps.storageClient) as T
            }
    }
}
