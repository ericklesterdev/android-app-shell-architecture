package com.nbahub.feature.teams.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nbahub.feature.teams.R
import com.nbahub.feature.teams.data.model.Team
import com.nbahub.feature.teams.ui.teamColor
import com.nbahub.platform.design.favoriteAccent
import com.nbahub.platform.design.favoriteContainer
import com.nbahub.platform.design.onFavoriteContainer
import com.nbahub.platform.design.onFavoriteContainerVariant

@Composable
internal fun TeamCard(
    team: Team,
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (isFavorite) MaterialTheme.colorScheme.favoriteContainer else MaterialTheme.colorScheme.surface
    val contentColor = if (isFavorite) MaterialTheme.colorScheme.onFavoriteContainer else MaterialTheme.colorScheme.onSurface

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TeamLogo(
                abbreviation = team.abbreviation,
                teamColor = teamColor(team.abbreviation),
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = team.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    if (isFavorite) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(R.string.teams_favorite_icon_description),
                            tint = MaterialTheme.colorScheme.favoriteAccent,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    InfoChip(label = displayConference(team.conference), isFavorite = isFavorite)
                    InfoChip(label = team.division, isFavorite = isFavorite)
                }
            }
        }
    }
}

@Composable
private fun InfoChip(label: String, isFavorite: Boolean) {
    val chipColor = if (isFavorite) {
        MaterialTheme.colorScheme.onFavoriteContainer.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = chipColor,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

private fun displayConference(conference: String): String = when (conference) {
    "East" -> "Eastern"
    "West" -> "Western"
    else -> conference
}
