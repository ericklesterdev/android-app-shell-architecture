package com.nbahub.feature.teams.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Player(
    val id: Int,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    val position: String,
    val height: String? = null,
    val weight: String? = null,
    @SerialName("jersey_number") val jerseyNumber: String? = null,
    val college: String? = null,
    val country: String? = null,
    @SerialName("draft_year") val draftYear: Int? = null,
    @SerialName("draft_round") val draftRound: Int? = null,
    @SerialName("draft_number") val draftNumber: Int? = null,
    val team: Team? = null,
)
