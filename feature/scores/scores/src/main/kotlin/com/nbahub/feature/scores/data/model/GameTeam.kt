package com.nbahub.feature.scores.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GameTeam(
    val id: Int,
    val conference: String,
    val division: String,
    val city: String,
    val name: String,
    @SerialName("full_name") val fullName: String,
    val abbreviation: String,
)
