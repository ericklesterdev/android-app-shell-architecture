package com.nbahub.feature.teams.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TeamsResponse(
    val data: List<Team>,
)
