package com.nbahub.feature.scores.data.model

import kotlinx.serialization.Serializable

@Serializable
internal data class GamesResponse(val data: List<Game>)
