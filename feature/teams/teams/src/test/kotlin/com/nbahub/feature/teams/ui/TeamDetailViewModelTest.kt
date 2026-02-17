package com.nbahub.feature.teams.ui

import app.cash.turbine.test
import com.nbahub.feature.teams.data.TeamsService
import com.nbahub.feature.teams.fake.FakeNetworkClient
import com.nbahub.feature.teams.fake.FakeStorageClient
import com.nbahub.feature.teams.testdata.player
import com.nbahub.feature.teams.testdata.playersResponse
import com.nbahub.feature.teams.testdata.team
import com.nbahub.feature.teams.testdata.teamsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TeamDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var networkClient: FakeNetworkClient
    private lateinit var storageClient: FakeStorageClient
    private lateinit var service: TeamsService

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        networkClient = FakeNetworkClient()
        storageClient = FakeStorageClient()
        service = TeamsService(networkClient)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(teamId: Int = 1): TeamDetailViewModel {
        return TeamDetailViewModel(teamId, service, storageClient)
    }

    private fun enqueueTeamAndPlayers(
        teamId: Int = 1,
        team: com.nbahub.feature.teams.data.model.Team = team(id = teamId),
        players: List<com.nbahub.feature.teams.data.model.Player> = listOf(player()),
    ) {
        networkClient.enqueue("v1/teams/$teamId", teamsResponse(team))
        networkClient.enqueue("v1/players", playersResponse(*players.toTypedArray()))
    }

    @Test
    fun `initial state is Loading`() {
        enqueueTeamAndPlayers()

        val vm = createViewModel()

        assertTrue(vm.uiState.value is TeamDetailUiState.Loading)
    }

    @Test
    fun `loads team and players on init`() = runTest {
        val celtics = team(id = 1, name = "Celtics", city = "Boston")
        val tatum = player(id = 1, firstName = "Jayson", lastName = "Tatum")
        val brown = player(id = 2, firstName = "Jaylen", lastName = "Brown")
        enqueueTeamAndPlayers(teamId = 1, team = celtics, players = listOf(tatum, brown))

        val vm = createViewModel(teamId = 1)
        advanceUntilIdle()

        val state = vm.uiState.value as TeamDetailUiState.Success
        assertEquals(celtics, state.team)
        assertEquals(listOf(tatum, brown), state.players)
    }

    @Test
    fun `isFavorite is false when team is not favorited`() = runTest {
        enqueueTeamAndPlayers()

        val vm = createViewModel()
        advanceUntilIdle()

        val state = vm.uiState.value as TeamDetailUiState.Success
        assertFalse(state.isFavorite)
    }

    @Test
    fun `isFavorite is true when team is already favorited`() = runTest {
        enqueueTeamAndPlayers(teamId = 1)
        storageClient.setFavorites(setOf(1))

        val vm = createViewModel(teamId = 1)
        advanceUntilIdle()

        val state = vm.uiState.value as TeamDetailUiState.Success
        assertTrue(state.isFavorite)
    }

    @Test
    fun `toggleFavorite adds team to favorites`() = runTest {
        enqueueTeamAndPlayers(teamId = 1)

        val vm = createViewModel(teamId = 1)
        advanceUntilIdle()

        vm.toggleFavorite()
        advanceUntilIdle()

        val state = vm.uiState.value as TeamDetailUiState.Success
        assertTrue(state.isFavorite)
    }

    @Test
    fun `toggleFavorite removes team from favorites`() = runTest {
        enqueueTeamAndPlayers(teamId = 1)
        storageClient.setFavorites(setOf(1))

        val vm = createViewModel(teamId = 1)
        advanceUntilIdle()

        vm.toggleFavorite()
        advanceUntilIdle()

        val state = vm.uiState.value as TeamDetailUiState.Success
        assertFalse(state.isFavorite)
    }

    @Test
    fun `favorite state updates reactively from storage`() = runTest {
        enqueueTeamAndPlayers(teamId = 1)

        val vm = createViewModel(teamId = 1)
        advanceUntilIdle()

        assertFalse((vm.uiState.value as TeamDetailUiState.Success).isFavorite)

        storageClient.setFavorites(setOf(1))
        advanceUntilIdle()

        assertTrue((vm.uiState.value as TeamDetailUiState.Success).isFavorite)

        storageClient.setFavorites(emptySet())
        advanceUntilIdle()

        assertFalse((vm.uiState.value as TeamDetailUiState.Success).isFavorite)
    }

    @Test
    fun `error state when network fails`() = runTest {
        networkClient.exception = RuntimeException("Network error")
        enqueueTeamAndPlayers()

        val vm = createViewModel()
        advanceUntilIdle()

        val state = vm.uiState.value as TeamDetailUiState.Error
        assertEquals("Network error", state.message)
    }

    @Test
    fun `uiState emits Loading then Success`() = runTest {
        enqueueTeamAndPlayers()

        val vm = createViewModel()

        vm.uiState.test {
            assertTrue(awaitItem() is TeamDetailUiState.Loading)

            advanceUntilIdle()

            assertTrue(awaitItem() is TeamDetailUiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
