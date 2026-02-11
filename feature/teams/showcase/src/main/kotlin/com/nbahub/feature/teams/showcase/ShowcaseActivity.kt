package com.nbahub.feature.teams.showcase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.nbahub.feature.teams.TeamsFeature
import com.nbahub.feature.teams.TeamsFeatureConfig
import com.nbahub.platform.network.mock.MockNetworkClient
import com.nbahub.platform.storage.DataStoreStorageClient

class ShowcaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val teamsFeature = TeamsFeature(
            TeamsFeatureConfig(
                networkClient = MockNetworkClient(applicationContext),
                storageClient = DataStoreStorageClient(applicationContext),
            )
        )

        setContent {
            MaterialTheme {
                teamsFeature.TeamsScreen()
            }
        }
    }
}
