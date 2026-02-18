package com.nbahub.feature.scores.data

import com.nbahub.feature.scores.data.model.Game
import com.nbahub.feature.scores.data.model.GamesResponse
import com.nbahub.platform.network.NetworkClient
import javax.inject.Inject

internal class ScoresService @Inject constructor(
    private val networkClient: NetworkClient,
) {

    suspend fun getGames(): List<Game> =
        networkClient.get("v1/games", deserializer = GamesResponse.serializer()).data
}
