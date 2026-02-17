package com.nbahub.feature.teams.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.nbahub.feature.teams.R

private val StarOrange = Color(0xFFFF9800)

@Composable
internal fun FavoriteFab(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = if (isFavorite) StarOrange else Color.White,
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = if (isFavorite) {
                stringResource(R.string.teams_detail_favorite_remove)
            } else {
                stringResource(R.string.teams_detail_favorite_add)
            },
            tint = if (isFavorite) Color.White else StarOrange,
        )
    }
}
