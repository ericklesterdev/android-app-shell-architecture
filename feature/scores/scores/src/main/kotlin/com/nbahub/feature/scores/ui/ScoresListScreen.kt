package com.nbahub.feature.scores.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nbahub.feature.scores.R
import com.nbahub.feature.scores.ui.components.GameCard

@Composable
internal fun ScoresListScreen(
    modifier: Modifier = Modifier,
    viewModel: ScoresListViewModel = viewModel(factory = LocalScoresViewModelFactory.current),
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
                Text(
                    text = stringResource(R.string.scores_error, uiState.error.orEmpty()),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        else -> {
            ScoresListContent(
                uiState = uiState,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun ScoresListContent(
    uiState: ScoresListUiState,
    modifier: Modifier = Modifier,
) {
    val favoriteTeamIds = uiState.favoriteTeamIds
    val yourTeamGames = uiState.games.filter { game ->
        game.homeTeam.id in favoriteTeamIds || game.visitorTeam.id in favoriteTeamIds
    }
    val allGames = uiState.games.filter { game ->
        game.homeTeam.id !in favoriteTeamIds && game.visitorTeam.id !in favoriteTeamIds
    }
    val liveCount = uiState.games.count { it.gameStatus() is GameStatus.Live }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.scores_header),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.scores_live_count, liveCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
        }

        if (yourTeamGames.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.scores_section_your_teams),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
            }
            items(yourTeamGames, key = { "fav_${it.id}" }) { game ->
                GameCard(game = game, isHighlighted = true)
                Spacer(Modifier.height(12.dp))
            }
        }

        items(allGames, key = { "game_${it.id}" }) { game ->
            GameCard(game = game, isHighlighted = false)
            Spacer(Modifier.height(12.dp))
        }
    }
}
