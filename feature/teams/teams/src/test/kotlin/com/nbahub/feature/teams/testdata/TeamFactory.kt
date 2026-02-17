package com.nbahub.feature.teams.testdata

import com.nbahub.feature.teams.data.model.Player
import com.nbahub.feature.teams.data.model.PlayersResponse
import com.nbahub.feature.teams.data.model.Team
import com.nbahub.feature.teams.data.model.TeamsResponse

@Suppress("LongParameterList")
internal fun team(
    id: Int = 1,
    conference: String = "East",
    division: String = "Atlantic",
    city: String = "Boston",
    name: String = "Celtics",
    fullName: String = "Boston Celtics",
    abbreviation: String = "BOS",
) = Team(
    id = id,
    conference = conference,
    division = division,
    city = city,
    name = name,
    fullName = fullName,
    abbreviation = abbreviation,
)

internal fun player(
    id: Int = 1,
    firstName: String = "Jayson",
    lastName: String = "Tatum",
    position: String = "F",
    team: Team? = null,
) = Player(
    id = id,
    firstName = firstName,
    lastName = lastName,
    position = position,
    team = team,
)

internal fun teamsResponse(vararg teams: Team = arrayOf(team())) =
    TeamsResponse(data = teams.toList())

internal fun playersResponse(vararg players: Player = arrayOf(player())) =
    PlayersResponse(data = players.toList())
