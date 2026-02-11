package com.nbahub.feature.teams.data

import com.nbahub.feature.teams.fake.FakeNetworkClient
import com.nbahub.feature.teams.testdata.player
import com.nbahub.feature.teams.testdata.playersResponse
import com.nbahub.feature.teams.testdata.team
import com.nbahub.feature.teams.testdata.teamsResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class TeamsServiceTest {

    private lateinit var networkClient: FakeNetworkClient
    private lateinit var service: TeamsService

    @Before
    fun setUp() {
        networkClient = FakeNetworkClient()
        service = TeamsService(networkClient)
    }

    @Test
    fun `getTeams returns list from network`() = runTest {
        val celtics = team(id = 1, name = "Celtics")
        val lakers = team(id = 2, name = "Lakers")
        networkClient.enqueue("v1/teams", teamsResponse(celtics, lakers))

        val result = service.getTeams()

        assertEquals(listOf(celtics, lakers), result)
    }

    @Test
    fun `getTeams calls correct path`() = runTest {
        networkClient.enqueue("v1/teams", teamsResponse())

        service.getTeams()

        assertEquals("v1/teams", networkClient.lastPath)
    }

    @Test
    fun `getTeams returns empty list when response has no teams`() = runTest {
        networkClient.enqueue("v1/teams", teamsResponse(*emptyArray()))

        val result = service.getTeams()

        assertTrue(result.isEmpty())
    }

    @Test(expected = IOException::class)
    fun `getTeams propagates exception`() = runTest {
        networkClient.exception = IOException("network failure")

        service.getTeams()
    }

    @Test
    fun `getTeam returns single team`() = runTest {
        val celtics = team(id = 1, name = "Celtics")
        networkClient.enqueue("v1/teams/1", teamsResponse(celtics))

        val result = service.getTeam(1)

        assertEquals(celtics, result)
    }

    @Test
    fun `getTeam calls correct path with id`() = runTest {
        networkClient.enqueue("v1/teams/42", teamsResponse(team(id = 42)))

        service.getTeam(42)

        assertEquals("v1/teams/42", networkClient.lastPath)
    }

    @Test
    fun `getPlayersByTeam returns players`() = runTest {
        val tatum = player(id = 1, firstName = "Jayson", lastName = "Tatum")
        val brown = player(id = 2, firstName = "Jaylen", lastName = "Brown")
        networkClient.enqueue("v1/players", playersResponse(tatum, brown))

        val result = service.getPlayersByTeam(1)

        assertEquals(listOf(tatum, brown), result)
    }

    @Test
    fun `getPlayersByTeam passes correct query params`() = runTest {
        networkClient.enqueue("v1/players", playersResponse())

        service.getPlayersByTeam(5)

        assertEquals(mapOf("team_ids[]" to "5"), networkClient.lastQueryParams)
    }

    @Test
    fun `getPlayersByTeam returns empty list when no players`() = runTest {
        networkClient.enqueue("v1/players", playersResponse(*emptyArray()))

        val result = service.getPlayersByTeam(1)

        assertTrue(result.isEmpty())
    }

    @Test(expected = IOException::class)
    fun `getPlayersByTeam propagates exception`() = runTest {
        networkClient.exception = IOException("network failure")

        service.getPlayersByTeam(1)
    }
}
