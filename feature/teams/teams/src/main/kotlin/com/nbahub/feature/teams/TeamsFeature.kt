package com.nbahub.feature.teams

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nbahub.feature.teams.data.TeamsService
import com.nbahub.platform.network.NetworkClient
import com.nbahub.platform.storage.StorageClient

data class TeamsFeatureConfig(
    val networkClient: NetworkClient,
    val storageClient: StorageClient,
)

class TeamsFeature(private val config: TeamsFeatureConfig) {

    internal val teamsService = TeamsService(config.networkClient)
    internal val storageClient = config.storageClient

    @Composable
    fun TeamsScreen() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text("Teams feature coming soon")
        }
    }
}
