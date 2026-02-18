package com.nbahub.feature.scores.di

import com.nbahub.feature.scores.ScoresFeatureDependencies
import com.nbahub.platform.network.NetworkClient
import com.nbahub.platform.storage.StorageClient
import dagger.Module
import dagger.Provides

@Module
internal class PlatformModule(
    private val dependencies: ScoresFeatureDependencies,
) {

    @Provides
    fun provideNetworkClient(): NetworkClient = dependencies.networkClient

    @Provides
    fun provideStorageClient(): StorageClient = dependencies.storageClient
}
