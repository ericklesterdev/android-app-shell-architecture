package com.nbahub.platform.network.mock

import android.content.Context
import com.nbahub.platform.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.FileNotFoundException

class MockNetworkClient(private val context: Context) : NetworkClient {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun <T> get(
        path: String,
        queryParams: Map<String, String>,
        deserializer: DeserializationStrategy<T>,
    ): T = withContext(Dispatchers.IO) {
        val basePath = "mock/${path.replace("/", "_")}"
        val jsonString = if (queryParams.isNotEmpty()) {
            val suffix = queryParams.entries
                .sortedBy { it.key }
                .joinToString("_") { (key, value) ->
                    "${key.replace("[", "").replace("]", "")}_$value"
                }
            tryReadAsset("${basePath}_${suffix}.json")
                ?: tryReadAsset("$basePath.json")
                ?: detailFallback(path)
                ?: throw FileNotFoundException("Mock not found: $basePath")
        } else {
            tryReadAsset("$basePath.json")
                ?: detailFallback(path)
                ?: throw FileNotFoundException("Mock not found: $basePath")
        }
        json.decodeFromString(deserializer, jsonString)
    }

    private fun tryReadAsset(assetPath: String): String? = try {
        context.assets.open(assetPath).bufferedReader().use { it.readText() }
    } catch (_: FileNotFoundException) {
        null
    }

    @Suppress("ReturnCount")
    private fun detailFallback(path: String): String? {
        val segments = path.trimEnd('/').split("/")
        val idStr = segments.lastOrNull() ?: return null
        val id = idStr.toIntOrNull() ?: return null
        val parentPath = segments.dropLast(1).joinToString("/")
        val parentAssetPath = "mock/${parentPath.replace("/", "_")}.json"
        val parentJson = try {
            context.assets.open(parentAssetPath).bufferedReader().use { it.readText() }
        } catch (_: FileNotFoundException) {
            return null
        }
        val root = json.parseToJsonElement(parentJson).jsonObject
        val dataArray = root["data"]?.jsonArray ?: return null
        val match = dataArray.firstOrNull { element ->
            (element as? JsonObject)?.get("id")?.jsonPrimitive?.int == id
        } ?: return null
        return """{"data":[$match]}"""
    }
}
