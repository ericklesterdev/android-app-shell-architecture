package com.nbahub.feature.teams.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nbahub.feature.teams.R
import com.nbahub.feature.teams.data.model.Player
import com.nbahub.feature.teams.data.model.Team
import com.nbahub.feature.teams.ui.components.FavoriteFab
import com.nbahub.feature.teams.ui.components.RosterItem
import com.nbahub.feature.teams.ui.components.TeamBanner

@Composable
internal fun TeamDetailScreen(
    teamId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TeamDetailViewModel = provideViewModel(
        key = "team_detail_$teamId",
    ) { deps -> TeamDetailViewModel.factory(teamId, deps) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is TeamDetailUiState.Loading -> TeamDetailLoading(modifier)
        is TeamDetailUiState.Error -> TeamDetailError(state.message, onRetry = viewModel::retry, modifier = modifier)
        is TeamDetailUiState.Success -> TeamDetailContent(
            team = state.team,
            players = state.players,
            isFavorite = state.isFavorite,
            onToggleFavorite = viewModel::toggleFavorite,
            modifier = modifier,
        )
    }
}

@Composable
private fun TeamDetailLoading(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun TeamDetailError(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = message, color = MaterialTheme.colorScheme.error)
            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.teams_retry))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TeamDetailContent(
    team: Team,
    players: List<Player>,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                TeamBanner(
                    abbreviation = team.abbreviation,
                    city = team.city,
                    name = team.name,
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.teams_detail_team_info),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            InfoChip(
                                label = stringResource(
                                    R.string.teams_detail_conference,
                                    displayConference(team.conference),
                                ),
                            )
                            InfoChip(
                                label = stringResource(
                                    R.string.teams_detail_division,
                                    team.division,
                                ),
                            )
                            InfoChip(
                                label = stringResource(
                                    R.string.teams_detail_city,
                                    team.city,
                                ),
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(modifier = Modifier.padding(vertical = 16.dp)) {
                        Text(
                            text = stringResource(R.string.teams_detail_roster),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        if (players.isEmpty()) {
                            Text(
                                text = stringResource(R.string.teams_detail_no_roster),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        } else {
                            players.forEachIndexed { index, player ->
                                if (index > 0) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                    )
                                }
                                RosterItem(player = player)
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }

        FavoriteFab(
            isFavorite = isFavorite,
            onClick = onToggleFavorite,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        )
    }
}

@Composable
private fun InfoChip(label: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun displayConference(conference: String): String = when (conference) {
    "East" -> stringResource(R.string.teams_conference_eastern)
    "West" -> stringResource(R.string.teams_conference_western)
    else -> conference
}
