package com.nbahub.feature.scores.ui

import com.nbahub.feature.scores.testdata.game
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameStatusTest {

    @Test
    fun `final game returns Final status`() {
        val g = game(status = "Final", period = 4, time = "")
        assertTrue(g.gameStatus() is GameStatus.Final)
    }

    @Test
    fun `upcoming game returns Upcoming status with start time`() {
        val g = game(status = "7:30 pm ET", period = 0, time = "")
        val status = g.gameStatus()
        assertTrue(status is GameStatus.Upcoming)
        assertEquals("7:30 pm ET", (status as GameStatus.Upcoming).startTime)
    }

    @Test
    fun `live game in 1st quarter returns Live status`() {
        val g = game(status = "1st Qtr", period = 1, time = "8:15")
        val status = g.gameStatus()
        assertTrue(status is GameStatus.Live)
        val live = status as GameStatus.Live
        assertEquals(1, live.period)
        assertEquals("8:15", live.time)
        assertEquals("1st Qtr", live.rawStatus)
    }

    @Test
    fun `live game in 4th quarter returns Live status`() {
        val g = game(status = "4th Qtr", period = 4, time = "2:34")
        val status = g.gameStatus()
        assertTrue(status is GameStatus.Live)
        val live = status as GameStatus.Live
        assertEquals(4, live.period)
        assertEquals("2:34", live.time)
    }

    @Test
    fun `halftime returns Live status`() {
        val g = game(status = "Halftime", period = 2, time = "")
        val status = g.gameStatus()
        assertTrue(status is GameStatus.Live)
        assertEquals("Halftime", (status as GameStatus.Live).rawStatus)
    }

    @Test
    fun `live game with empty time returns Live status`() {
        val g = game(status = "3rd Qtr", period = 3, time = "")
        val status = g.gameStatus()
        assertTrue(status is GameStatus.Live)
        val live = status as GameStatus.Live
        assertEquals(3, live.period)
        assertEquals("", live.time)
    }
}
