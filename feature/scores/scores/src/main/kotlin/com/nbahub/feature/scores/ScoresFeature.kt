package com.nbahub.feature.scores

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.nbahub.feature.scores.di.ScoresComponent
import com.nbahub.feature.scores.ui.LocalScoresViewModelFactory
import com.nbahub.feature.scores.ui.ScoresListScreen
import com.nbahub.platform.network.NetworkClient
import com.nbahub.platform.storage.StorageClient

data class ScoresFeatureDependencies(
    val networkClient: NetworkClient,
    val storageClient: StorageClient,
)

@Composable
fun ScoresScreen(
    dependencies: ScoresFeatureDependencies,
    modifier: Modifier = Modifier,
    onTeamClick: (Int) -> Unit = {},
) {
    val factory = remember(dependencies) {
        ScoresComponent.create(dependencies).viewModelFactory()
    }
    CompositionLocalProvider(LocalScoresViewModelFactory provides factory) {
        ScoresListScreen(modifier = modifier)
    }
}
