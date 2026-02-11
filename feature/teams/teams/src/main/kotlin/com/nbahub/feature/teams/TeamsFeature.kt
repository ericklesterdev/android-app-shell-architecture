package com.nbahub.feature.teams

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nbahub.feature.teams.data.TeamsService
import com.nbahub.feature.teams.ui.TeamsListScreen
import com.nbahub.feature.teams.ui.TeamsListViewModel
import com.nbahub.platform.design.NbaHubTheme
import com.nbahub.platform.network.NetworkClient
import com.nbahub.platform.storage.StorageClient

data class TeamsFeatureDependencies(
    val networkClient: NetworkClient,
    val storageClient: StorageClient,
)

@Composable
fun TeamsScreen(
    dependencies: TeamsFeatureDependencies,
    modifier: Modifier = Modifier,
    onTeamClick: (Int) -> Unit = {},
) {
    val teamsService = remember { TeamsService(dependencies.networkClient) }

    NbaHubTheme {
        val viewModel: TeamsListViewModel = viewModel(
            factory = TeamsListViewModel.factory(teamsService, dependencies.storageClient)
        )
        TeamsListScreen(viewModel = viewModel, onTeamClick = onTeamClick, modifier = modifier)
    }
}
