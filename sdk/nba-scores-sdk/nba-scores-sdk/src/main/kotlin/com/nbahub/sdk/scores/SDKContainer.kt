package com.nbahub.sdk.scores

import com.nbahub.feature.scores.ScoresFeatureDependencies
import com.nbahub.platform.network.OkHttpNetworkClient
import com.nbahub.platform.network.mock.MockNetworkClient
import com.nbahub.platform.storage.DataStoreStorageClient

internal class SDKContainer(config: NbaSDKConfig) {
    val scoresFeatureDependencies = ScoresFeatureDependencies(
        networkClient = if (config.useMockData) {
            MockNetworkClient(config.context)
        } else {
            OkHttpNetworkClient(
                baseUrl = config.environment.baseUrl,
                apiKey = config.apiKey,
            )
        },
        storageClient = DataStoreStorageClient(config.context),
    )
}
