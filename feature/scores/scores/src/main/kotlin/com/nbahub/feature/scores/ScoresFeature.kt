package com.nbahub.feature.scores

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nbahub.platform.network.NetworkClient
import com.nbahub.platform.storage.StorageClient

data class ScoresFeatureDependencies(
    val networkClient: NetworkClient,
    val storageClient: StorageClient,
)

internal val LocalScoresDependencies = staticCompositionLocalOf<ScoresFeatureDependencies> {
    error("ScoresFeatureDependencies not provided")
}

@Composable
fun ScoresScreen(
    dependencies: ScoresFeatureDependencies,
    modifier: Modifier = Modifier,
    onTeamClick: (Int) -> Unit = {},
) {
    CompositionLocalProvider(LocalScoresDependencies provides dependencies) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.scores_placeholder),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}
