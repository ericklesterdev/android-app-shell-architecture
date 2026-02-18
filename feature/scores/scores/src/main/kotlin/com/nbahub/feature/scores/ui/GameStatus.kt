package com.nbahub.feature.scores.ui

import com.nbahub.feature.scores.data.model.Game

internal sealed class GameStatus {
    data class Live(val period: Int, val time: String, val rawStatus: String) : GameStatus()
    data object Final : GameStatus()
    data class Upcoming(val startTime: String) : GameStatus()
}

internal fun Game.gameStatus(): GameStatus = when {
    status == "Final" -> GameStatus.Final
    period == 0 -> GameStatus.Upcoming(status)
    else -> GameStatus.Live(period = period, time = time, rawStatus = status)
}
