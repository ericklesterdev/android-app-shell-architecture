package com.nbahub.feature.teams.data.model

import kotlinx.serialization.Serializable

@Serializable
internal data class PlayersResponse(
    val data: List<Player>,
)
