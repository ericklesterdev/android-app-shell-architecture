package com.nbahub.platform.contracts

import kotlinx.serialization.DeserializationStrategy

interface NetworkClient {
    suspend fun <T> get(
        path: String,
        queryParams: Map<String, String> = emptyMap(),
        deserializer: DeserializationStrategy<T>,
    ): T
}
