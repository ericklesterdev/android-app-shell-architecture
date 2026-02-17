package com.nbahub.feature.teams

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nbahub.feature.teams.ui.TeamDetailScreen
import com.nbahub.feature.teams.ui.TeamsListScreen
import com.nbahub.platform.design.NbaHubTheme
import com.nbahub.platform.network.NetworkClient
import com.nbahub.platform.storage.StorageClient

data class TeamsFeatureDependencies(
    val networkClient: NetworkClient,
    val storageClient: StorageClient,
)

internal val LocalTeamsDependencies = staticCompositionLocalOf<TeamsFeatureDependencies> {
    error("TeamsFeatureDependencies not provided")
}

private object TeamsDestinations {
    const val LIST = "teams_list"
    const val DETAIL = "teams_detail/{teamId}"
    fun detail(teamId: Int) = "teams_detail/$teamId"
}

@Composable
fun TeamsScreen(
    dependencies: TeamsFeatureDependencies,
    modifier: Modifier = Modifier,
    onTeamClick: (Int) -> Unit = {},
) {
    NbaHubTheme {
        CompositionLocalProvider(LocalTeamsDependencies provides dependencies) {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = TeamsDestinations.LIST,
                modifier = modifier,
            ) {
                composable(TeamsDestinations.LIST) {
                    TeamsListScreen(
                        onTeamClick = { teamId ->
                            onTeamClick(teamId)
                            navController.navigate(TeamsDestinations.detail(teamId))
                        },
                    )
                }
                composable(
                    route = TeamsDestinations.DETAIL,
                    arguments = listOf(navArgument("teamId") { type = NavType.IntType }),
                ) { backStackEntry ->
                    val teamId = backStackEntry.arguments?.getInt("teamId") ?: return@composable
                    TeamDetailScreen(
                        teamId = teamId,
                        onBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}
