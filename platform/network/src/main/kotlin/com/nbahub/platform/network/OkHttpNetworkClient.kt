package com.nbahub.platform.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class OkHttpNetworkClient(
    private val baseUrl: String,
    private val apiKey: String,
) : NetworkClient {

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun <T> get(
        path: String,
        queryParams: Map<String, String>,
        deserializer: DeserializationStrategy<T>,
    ): T = withContext(Dispatchers.IO) {
        val urlBuilder = "$baseUrl/$path".toHttpUrl().newBuilder()
        queryParams.forEach { (key, value) -> urlBuilder.addQueryParameter(key, value) }

        val request = Request.Builder()
            .url(urlBuilder.build())
            .addHeader("Authorization", apiKey)
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: error("Empty response body for $path")
        json.decodeFromString(deserializer, body)
    }
}
