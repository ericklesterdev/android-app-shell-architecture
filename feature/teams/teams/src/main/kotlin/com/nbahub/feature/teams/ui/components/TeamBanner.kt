package com.nbahub.feature.teams.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nbahub.feature.teams.ui.teamColor
import androidx.compose.foundation.background

@Composable
internal fun TeamBanner(
    abbreviation: String,
    city: String,
    name: String,
    modifier: Modifier = Modifier,
) {
    val color = teamColor(abbreviation)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color)
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TeamLogo(
            abbreviation = abbreviation,
            teamColor = Color.White.copy(alpha = 0.2f),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = city,
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = name,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}
