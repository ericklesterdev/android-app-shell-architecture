package com.nbahub.feature.scores.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nbahub.feature.scores.data.model.GameTeam
import com.nbahub.feature.scores.ui.teamColor
import com.nbahub.platform.design.onFavoriteContainer
import com.nbahub.platform.design.onFavoriteContainerVariant

@Composable
internal fun ScoreTeamRow(
    team: GameTeam,
    score: String,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TeamLogo(
            abbreviation = team.abbreviation,
            teamColor = teamColor(team.abbreviation),
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = team.city,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) {
                    MaterialTheme.colorScheme.onFavoriteContainer
                } else {
                    Color.Unspecified
                },
            )
            Text(
                text = team.name,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isHighlighted) {
                    MaterialTheme.colorScheme.onFavoriteContainerVariant
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }

        Text(
            text = score,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = if (isHighlighted) {
                MaterialTheme.colorScheme.onFavoriteContainer
            } else {
                Color.Unspecified
            },
        )
    }
}
