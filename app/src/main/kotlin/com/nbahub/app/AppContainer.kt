package com.nbahub.app

import android.content.Context
import com.nbahub.feature.teams.TeamsFeatureDependencies
import com.nbahub.platform.network.NetworkClient
import com.nbahub.platform.network.OkHttpNetworkClient
import com.nbahub.platform.network.mock.MockNetworkClient
import com.nbahub.platform.storage.DataStoreStorageClient
import com.nbahub.platform.storage.StorageClient

class AppContainer(context: Context) {

    private val networkClient: NetworkClient = if (AppConfig.useMockNetwork) {
        MockNetworkClient(context)
    } else {
        OkHttpNetworkClient(
            baseUrl = AppConfig.baseUrl,
            apiKey = AppConfig.apiKey,
        )
    }

    private val storageClient: StorageClient = DataStoreStorageClient(context)

    val teamsFeatureDependencies = TeamsFeatureDependencies(
        networkClient = networkClient,
        storageClient = storageClient,
    )
}
