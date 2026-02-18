package com.nbahub.app

import android.content.Context
import com.nbahub.feature.scores.ScoresFeatureDependencies
import com.nbahub.feature.teams.TeamsFeatureDependencies
import com.nbahub.platform.network.NetworkClient
import com.nbahub.platform.network.OkHttpNetworkClient
import com.nbahub.platform.network.mock.MockNetworkClient
import com.nbahub.platform.storage.DataStoreStorageClient
import com.nbahub.platform.storage.StorageClient

class AppContainer(context: Context, config: AppConfig = AppConfig()) {

    private val networkClient: NetworkClient = if (config.useMockNetwork) {
        MockNetworkClient(context)
    } else {
        OkHttpNetworkClient(
            baseUrl = config.baseUrl,
            apiKey = config.apiKey,
        )
    }

    private val storageClient: StorageClient = DataStoreStorageClient(context)

    val teamsFeatureDependencies = TeamsFeatureDependencies(
        networkClient = networkClient,
        storageClient = storageClient,
    )

    val scoresFeatureDependencies = ScoresFeatureDependencies(
        networkClient = networkClient,
        storageClient = storageClient,
    )
}
