package com.nbahub.sdk.scores

import android.content.Context

enum class NbaEnvironment(internal val baseUrl: String) {
    PRODUCTION("https://api.balldontlie.io"),
}

data class NbaSDKConfig(
    val context: Context,
    val apiKey: String,
    val environment: NbaEnvironment = NbaEnvironment.PRODUCTION,
    val useMockData: Boolean = false,
)

object NbaSDK {
    internal lateinit var container: SDKContainer
        private set

    fun initialize(config: NbaSDKConfig): NbaSDK {
        container = SDKContainer(config)
        return this
    }
}
