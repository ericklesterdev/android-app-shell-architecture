package com.nbahub.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.nbahub.app.ui.theme.NbaHubTheme
import com.nbahub.feature.teams.TeamsFeatureDependencies
import com.nbahub.feature.teams.TeamsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = AppContainer(applicationContext)

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            NbaHubTheme(darkTheme = isDarkTheme) {
                NbaHubApp(
                    teamsFeatureDependencies = appContainer.teamsFeatureDependencies,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme },
                )
            }
        }
    }
}

private enum class Tab(val labelResId: Int) {
    Scores(R.string.tab_scores),
    Teams(R.string.tab_teams),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NbaHubApp(
    teamsFeatureDependencies: TeamsFeatureDependencies,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showBackButton by remember { mutableStateOf(false) }
    val tabs = Tab.entries
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                navigationIcon = {
                    if (showBackButton && tabs[selectedTab] == Tab.Teams) {
                        IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.navigate_back),
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            painter = if (isDarkTheme) {
                                painterResource(R.drawable.ic_light_mode)
                            } else {
                                painterResource(R.drawable.ic_dark_mode)
                            },
                            contentDescription = stringResource(R.string.toggle_theme),
                        )
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            showBackButton = false
                        },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    Tab.Scores -> Icons.Default.DateRange
                                    Tab.Teams -> Icons.Default.Info
                                },
                                contentDescription = stringResource(tab.labelResId),
                            )
                        },
                        label = { Text(stringResource(tab.labelResId)) },
                    )
                }
            }
        },
    ) { innerPadding ->
        when (tabs[selectedTab]) {
            Tab.Scores -> PlaceholderScreen(
                title = stringResource(R.string.scores_coming_soon),
                modifier = Modifier.padding(innerPadding),
            )
            Tab.Teams -> TeamsScreen(
                dependencies = teamsFeatureDependencies,
                modifier = Modifier.padding(innerPadding),
                onBackVisibleChange = { showBackButton = it },
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        )
    }
}
