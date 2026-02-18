package com.nbahub.feature.teams.showcase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.nbahub.feature.teams.TeamsFeatureDependencies
import com.nbahub.feature.teams.TeamsScreen
import com.nbahub.platform.design.NbaHubTheme
import com.nbahub.platform.network.mock.MockNetworkClient
import com.nbahub.platform.storage.DataStoreStorageClient

class ShowcaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dependencies = TeamsFeatureDependencies(
            networkClient = MockNetworkClient(applicationContext),
            storageClient = DataStoreStorageClient(applicationContext),
        )

        setContent {
            NbaHubTheme {
                Scaffold { innerPadding ->
                    TeamsScreen(
                        dependencies = dependencies,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
