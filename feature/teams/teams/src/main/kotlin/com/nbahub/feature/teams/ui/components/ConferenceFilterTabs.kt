package com.nbahub.feature.teams.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nbahub.feature.teams.ui.Conference

@Composable
internal fun ConferenceFilterTabs(
    selected: Conference,
    onSelect: (Conference) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    val borderColor = MaterialTheme.colorScheme.outline

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .border(1.dp, borderColor, shape)
            .clip(shape),
    ) {
        Conference.entries.forEachIndexed { index, conference ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(borderColor),
                )
            }

            val isSelected = conference == selected
            val backgroundColor = if (isSelected) {
                MaterialTheme.colorScheme.inverseSurface
            } else {
                Color.Transparent
            }
            val textColor = if (isSelected) {
                MaterialTheme.colorScheme.inverseOnSurface
            } else {
                MaterialTheme.colorScheme.onSurface
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(backgroundColor)
                    .clickable { onSelect(conference) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(conference.labelRes),
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
