package com.nbahub.feature.scores.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nbahub.feature.scores.R
import com.nbahub.feature.scores.data.model.Game
import com.nbahub.feature.scores.ui.GameStatus
import com.nbahub.feature.scores.ui.gameStatus
import com.nbahub.platform.design.favoriteAccent
import com.nbahub.platform.design.favoriteContainer
import com.nbahub.platform.design.onFavoriteContainerVariant
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
internal fun GameCard(
    game: Game,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier,
) {
    val status = game.gameStatus()
    val isUpcoming = status is GameStatus.Upcoming

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) {
                MaterialTheme.colorScheme.favoriteContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isHighlighted) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = stringResource(R.string.scores_favorite_description),
                        tint = MaterialTheme.colorScheme.favoriteAccent,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp),
                    )
                }
                Text(
                    text = formatDate(game.date),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isHighlighted) {
                        MaterialTheme.colorScheme.onFavoriteContainerVariant
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                StatusBadge(status = status)
            }

            Spacer(Modifier.height(8.dp))

            val upcomingScore = stringResource(R.string.scores_upcoming_score)

            ScoreTeamRow(
                team = game.visitorTeam,
                score = if (isUpcoming) upcomingScore else game.visitorTeamScore.toString(),
                isHighlighted = isHighlighted,
            )

            Spacer(Modifier.height(4.dp))

            ScoreTeamRow(
                team = game.homeTeam,
                score = if (isUpcoming) upcomingScore else game.homeTeamScore.toString(),
                isHighlighted = isHighlighted,
            )
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("EEE, MMM d", Locale.US)
        val date = inputFormat.parse(dateString) ?: return dateString
        outputFormat.format(date)
    } catch (_: Exception) {
        dateString
    }
}
