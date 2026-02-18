package com.nbahub.feature.scores.ui

import app.cash.turbine.test
import com.nbahub.feature.scores.data.ScoresService
import com.nbahub.feature.scores.fake.FakeNetworkClient
import com.nbahub.feature.scores.fake.FakeStorageClient
import com.nbahub.feature.scores.testdata.game
import com.nbahub.feature.scores.testdata.gameTeam
import com.nbahub.feature.scores.testdata.gamesResponse
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScoresListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var networkClient: FakeNetworkClient
    private lateinit var storageClient: FakeStorageClient
    private lateinit var service: ScoresService

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        networkClient = FakeNetworkClient()
        storageClient = FakeStorageClient()
        service = ScoresService(networkClient)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): ScoresListViewModel {
        return ScoresListViewModel(service, storageClient)
    }

    @Test
    fun `initial state is loading with empty games`() {
        networkClient.enqueue("v1/games", gamesResponse())

        val vm = createViewModel()

        val state = vm.uiState.value
        assertTrue(state.isLoading)
        assertTrue(state.games.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `games are loaded after initialization`() = runTest {
        val game1 = game(id = 1)
        val game2 = game(id = 2)
        networkClient.enqueue("v1/games", gamesResponse(game1, game2))

        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals(listOf(game1, game2), vm.uiState.value.games)
    }

    @Test
    fun `loading becomes false after games load`() = runTest {
        networkClient.enqueue("v1/games", gamesResponse())

        val vm = createViewModel()
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `error is set when loading fails`() = runTest {
        networkClient.exception = RuntimeException("network failure")

        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals("network failure", vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `favorite team ids observed from storage`() = runTest {
        networkClient.enqueue("v1/games", gamesResponse())
        storageClient.setFavorites(setOf(1, 5))

        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals(setOf(1, 5), vm.uiState.value.favoriteTeamIds)
    }

    @Test
    fun `favorite ids update on new emissions`() = runTest {
        networkClient.enqueue("v1/games", gamesResponse())

        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals(emptySet<Int>(), vm.uiState.value.favoriteTeamIds)

        storageClient.setFavorites(setOf(3))
        advanceUntilIdle()

        assertEquals(setOf(3), vm.uiState.value.favoriteTeamIds)
    }

    @Test
    fun `default favorites is empty set`() {
        networkClient.enqueue("v1/games", gamesResponse())

        val vm = createViewModel()

        assertEquals(emptySet<Int>(), vm.uiState.value.favoriteTeamIds)
    }

    @Test
    fun `uiState emits loading then loaded`() = runTest {
        val g = game(id = 1)
        networkClient.enqueue("v1/games", gamesResponse(g))

        val vm = createViewModel()

        vm.uiState.test {
            val loading = awaitItem()
            assertTrue(loading.isLoading)
            assertTrue(loading.games.isEmpty())

            advanceUntilIdle()

            val loaded = awaitItem()
            assertFalse(loaded.isLoading)
            assertEquals(listOf(g), loaded.games)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState reflects favorites update`() = runTest {
        networkClient.enqueue("v1/games", gamesResponse())

        val vm = createViewModel()
        advanceUntilIdle()

        vm.uiState.test {
            val initial = awaitItem()
            assertEquals(emptySet<Int>(), initial.favoriteTeamIds)

            storageClient.setFavorites(setOf(7))
            advanceUntilIdle()

            val updated = awaitItem()
            assertEquals(setOf(7), updated.favoriteTeamIds)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `games with favorite teams are identified correctly`() = runTest {
        val bosTeam = gameTeam(id = 2, abbreviation = "BOS")
        val lalTeam = gameTeam(id = 14, abbreviation = "LAL")
        val g = game(id = 1, homeTeam = bosTeam, visitorTeam = lalTeam)
        networkClient.enqueue("v1/games", gamesResponse(g))
        storageClient.setFavorites(setOf(2))

        val vm = createViewModel()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(1, state.games.size)
        assertTrue(state.games[0].homeTeam.id in state.favoriteTeamIds)
    }
}
