package com.nbahub.feature.teams.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nbahub.feature.teams.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nbahub.feature.teams.ui.components.ConferenceFilterTabs
import com.nbahub.feature.teams.ui.components.TeamCard

@Composable
internal fun TeamsListScreen(
    onTeamClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TeamsListViewModel = provideViewModel(factory = TeamsListViewModel::factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.teams_error, uiState.error.orEmpty()),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Button(onClick = viewModel::retry) {
                        Text(text = stringResource(R.string.teams_retry))
                    }
                }
            }
        }
        else -> TeamsListContent(uiState = uiState, onTeamClick = onTeamClick, onSelectConference = viewModel::selectConference, modifier = modifier)
    }
}

@Composable
private fun TeamsListContent(
    uiState: TeamsListUiState,
    onTeamClick: (Int) -> Unit,
    onSelectConference: (Conference) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filteredTeams = when (uiState.selectedConference) {
        Conference.ALL -> uiState.teams
        Conference.EASTERN -> uiState.teams.filter { it.conference == "East" }
        Conference.WESTERN -> uiState.teams.filter { it.conference == "West" }
    }

    val favoriteTeams = filteredTeams.filter { it.id in uiState.favoriteTeamIds }
    val allTeams = filteredTeams.filter { it.id !in uiState.favoriteTeamIds }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.teams_header),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(12.dp))
        }

        item {
            ConferenceFilterTabs(
                selected = uiState.selectedConference,
                onSelect = onSelectConference,
            )
            Spacer(Modifier.height(16.dp))
        }

        if (favoriteTeams.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.teams_section_favorites),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
            }
            items(favoriteTeams, key = { "fav_${it.id}" }) { team ->
                TeamCard(
                    team = team,
                    isFavorite = true,
                    onClick = { onTeamClick(team.id) },
                )
                Spacer(Modifier.height(12.dp))
            }
            item {
                Spacer(Modifier.height(12.dp))
            }
        }

        if (favoriteTeams.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.teams_section_all),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
            }
        }

        items(allTeams, key = { "team_${it.id}" }) { team ->
            TeamCard(
                team = team,
                isFavorite = false,
                onClick = { onTeamClick(team.id) },
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}
