package com.nbahub.feature.teams.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nbahub.feature.teams.R
import com.nbahub.platform.design.favoriteAccent
import com.nbahub.platform.design.onFavoriteContainer

@Composable
internal fun FavoriteFab(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = if (isFavorite) MaterialTheme.colorScheme.favoriteAccent else MaterialTheme.colorScheme.surface,
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = if (isFavorite) {
                stringResource(R.string.teams_detail_favorite_remove)
            } else {
                stringResource(R.string.teams_detail_favorite_add)
            },
            tint = if (isFavorite) MaterialTheme.colorScheme.onFavoriteContainer else MaterialTheme.colorScheme.favoriteAccent,
        )
    }
}
