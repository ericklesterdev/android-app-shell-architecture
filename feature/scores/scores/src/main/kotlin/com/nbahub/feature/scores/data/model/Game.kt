package com.nbahub.feature.scores.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Game(
    val id: Int,
    val date: String,
    val season: Int,
    val status: String,
    val period: Int,
    val time: String,
    val postseason: Boolean,
    @SerialName("home_team_score") val homeTeamScore: Int,
    @SerialName("visitor_team_score") val visitorTeamScore: Int,
    @SerialName("home_team") val homeTeam: GameTeam,
    @SerialName("visitor_team") val visitorTeam: GameTeam,
)
