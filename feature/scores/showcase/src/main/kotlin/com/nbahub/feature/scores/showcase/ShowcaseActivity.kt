package com.nbahub.feature.scores.showcase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.nbahub.feature.scores.ScoresFeatureDependencies
import com.nbahub.feature.scores.ScoresScreen
import com.nbahub.platform.design.NbaHubTheme
import com.nbahub.platform.network.mock.MockNetworkClient
import com.nbahub.platform.storage.DataStoreStorageClient

class ShowcaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dependencies = ScoresFeatureDependencies(
            networkClient = MockNetworkClient(applicationContext),
            storageClient = DataStoreStorageClient(applicationContext),
        )

        setContent {
            NbaHubTheme {
                Scaffold { innerPadding ->
                    ScoresScreen(
                        dependencies = dependencies,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
