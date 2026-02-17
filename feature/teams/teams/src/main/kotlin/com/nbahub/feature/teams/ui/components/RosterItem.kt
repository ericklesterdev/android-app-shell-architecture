package com.nbahub.feature.teams.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nbahub.feature.teams.R
import com.nbahub.feature.teams.data.model.Player

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun RosterItem(
    player: Player,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = "${player.firstName} ${player.lastName}",
            style = MaterialTheme.typography.bodyLarge,
        )
        FlowRow(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (player.position.isNotBlank()) {
                StatChip(label = player.position)
            }
            if (!player.height.isNullOrBlank()) {
                StatChip(label = player.height)
            }
            if (!player.weight.isNullOrBlank()) {
                StatChip(label = stringResource(R.string.teams_detail_weight, player.weight))
            }
        }
    }
}

@Composable
private fun StatChip(label: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
