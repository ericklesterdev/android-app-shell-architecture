package com.nbahub.feature.scores.testdata

import com.nbahub.feature.scores.data.model.Game
import com.nbahub.feature.scores.data.model.GameTeam
import com.nbahub.feature.scores.data.model.GamesResponse

@Suppress("LongParameterList")
internal fun gameTeam(
    id: Int = 1,
    conference: String = "East",
    division: String = "Atlantic",
    city: String = "Boston",
    name: String = "Celtics",
    fullName: String = "Boston Celtics",
    abbreviation: String = "BOS",
) = GameTeam(
    id = id,
    conference = conference,
    division = division,
    city = city,
    name = name,
    fullName = fullName,
    abbreviation = abbreviation,
)

@Suppress("LongParameterList")
internal fun game(
    id: Int = 1,
    date: String = "2024-12-15",
    season: Int = 2024,
    status: String = "Final",
    period: Int = 4,
    time: String = "",
    postseason: Boolean = false,
    homeTeamScore: Int = 100,
    visitorTeamScore: Int = 98,
    homeTeam: GameTeam = gameTeam(id = 1, city = "Boston", name = "Celtics", abbreviation = "BOS"),
    visitorTeam: GameTeam = gameTeam(id = 2, city = "Los Angeles", name = "Lakers", abbreviation = "LAL"),
) = Game(
    id = id,
    date = date,
    season = season,
    status = status,
    period = period,
    time = time,
    postseason = postseason,
    homeTeamScore = homeTeamScore,
    visitorTeamScore = visitorTeamScore,
    homeTeam = homeTeam,
    visitorTeam = visitorTeam,
)

internal fun gamesResponse(vararg games: Game = arrayOf(game())) =
    GamesResponse(data = games.toList())
