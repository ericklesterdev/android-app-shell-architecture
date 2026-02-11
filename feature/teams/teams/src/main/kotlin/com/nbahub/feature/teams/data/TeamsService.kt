package com.nbahub.feature.teams.data

import com.nbahub.feature.teams.data.model.Player
import com.nbahub.feature.teams.data.model.PlayersResponse
import com.nbahub.feature.teams.data.model.Team
import com.nbahub.feature.teams.data.model.TeamsResponse
import com.nbahub.platform.network.NetworkClient

class TeamsService(private val networkClient: NetworkClient) {

    suspend fun getTeams(): List<Team> =
        networkClient.get("v1/teams", deserializer = TeamsResponse.serializer()).data

    suspend fun getTeam(id: Int): Team =
        networkClient.get("v1/teams/$id", deserializer = Team.serializer())

    suspend fun getPlayersByTeam(teamId: Int): List<Player> =
        networkClient.get(
            "v1/players",
            queryParams = mapOf("team_ids[]" to teamId.toString()),
            deserializer = PlayersResponse.serializer(),
        ).data
}
