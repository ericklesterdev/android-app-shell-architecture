package com.nbahub.feature.teams.fake

import com.nbahub.platform.network.NetworkClient
import kotlinx.serialization.DeserializationStrategy

internal class FakeNetworkClient : NetworkClient {

    private val responses = mutableMapOf<String, Any>()
    var lastPath: String? = null
        private set
    var lastQueryParams: Map<String, String>? = null
        private set

    fun <T : Any> enqueue(path: String, response: T) {
        responses[path] = response
    }

    var exception: Exception? = null

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> get(
        path: String,
        queryParams: Map<String, String>,
        deserializer: DeserializationStrategy<T>,
    ): T {
        lastPath = path
        lastQueryParams = queryParams
        exception?.let { throw it }
        return responses[path] as? T
            ?: error("No response enqueued for path: $path")
    }
}
