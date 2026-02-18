package com.nbahub.feature.scores.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nbahub.feature.scores.R
import com.nbahub.feature.scores.ui.GameStatus
import com.nbahub.platform.design.onStatusBadge
import com.nbahub.platform.design.statusFinal
import com.nbahub.platform.design.statusLive

@Composable
internal fun StatusBadge(
    status: GameStatus,
    modifier: Modifier = Modifier,
) {
    when (status) {
        is GameStatus.Live -> FilledBadge(
            text = formatLiveClock(status),
            color = MaterialTheme.colorScheme.statusLive,
            modifier = modifier,
        )
        is GameStatus.Final -> FilledBadge(
            text = stringResource(R.string.scores_status_final),
            color = MaterialTheme.colorScheme.statusFinal,
            modifier = modifier,
        )
        is GameStatus.Upcoming -> OutlinedBadge(
            text = status.startTime,
            modifier = modifier,
        )
    }
}

@Composable
private fun formatLiveClock(status: GameStatus.Live): String = when (status.rawStatus) {
    "Halftime" -> stringResource(R.string.scores_status_halftime)
    else -> if (status.time.isNotBlank()) {
        stringResource(R.string.scores_status_quarter_with_time, status.period, status.time)
    } else {
        stringResource(R.string.scores_status_quarter, status.period)
    }
}

@Composable
private fun FilledBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = color,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onStatusBadge,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun OutlinedBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
        )
    }
}
