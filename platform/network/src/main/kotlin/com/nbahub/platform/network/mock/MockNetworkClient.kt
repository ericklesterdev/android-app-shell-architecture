package com.nbahub.platform.network.mock

import android.content.Context
import com.nbahub.platform.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json

class MockNetworkClient(private val context: Context) : NetworkClient {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun <T> get(
        path: String,
        queryParams: Map<String, String>,
        deserializer: DeserializationStrategy<T>,
    ): T = withContext(Dispatchers.IO) {
        val assetPath = "mock/${path.replace("/", "_")}.json"
        val jsonString = context.assets.open(assetPath).bufferedReader().use { it.readText() }
        json.decodeFromString(deserializer, jsonString)
    }
}
