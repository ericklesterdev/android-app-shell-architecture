package com.nbahub.feature.scores.data

import com.nbahub.feature.scores.fake.FakeNetworkClient
import com.nbahub.feature.scores.testdata.game
import com.nbahub.feature.scores.testdata.gamesResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class ScoresServiceTest {

    private lateinit var networkClient: FakeNetworkClient
    private lateinit var service: ScoresService

    @Before
    fun setUp() {
        networkClient = FakeNetworkClient()
        service = ScoresService(networkClient)
    }

    @Test
    fun `getGames returns list from network`() = runTest {
        val game1 = game(id = 1)
        val game2 = game(id = 2)
        networkClient.enqueue("v1/games", gamesResponse(game1, game2))

        val result = service.getGames()

        assertEquals(listOf(game1, game2), result)
    }

    @Test
    fun `getGames calls correct path`() = runTest {
        networkClient.enqueue("v1/games", gamesResponse())

        service.getGames()

        assertEquals("v1/games", networkClient.lastPath)
    }

    @Test
    fun `getGames returns empty list when response has no games`() = runTest {
        networkClient.enqueue("v1/games", gamesResponse(*emptyArray()))

        val result = service.getGames()

        assertTrue(result.isEmpty())
    }

    @Test(expected = IOException::class)
    fun `getGames propagates exception`() = runTest {
        networkClient.exception = IOException("network failure")

        service.getGames()
    }
}
