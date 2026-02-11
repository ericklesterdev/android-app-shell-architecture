package com.nbahub.feature.teams.ui

import app.cash.turbine.test
import com.nbahub.feature.teams.data.TeamsService
import com.nbahub.feature.teams.fake.FakeNetworkClient
import com.nbahub.feature.teams.fake.FakeStorageClient
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
class TeamsListViewModelTest {

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

    private fun createViewModel(): TeamsListViewModel {
        return TeamsListViewModel(service, storageClient)
    }

    @Test
    fun `initial state is loading with empty teams`() {
        networkClient.enqueue("v1/teams", teamsResponse())

        val vm = createViewModel()

        val state = vm.uiState.value
        assertTrue(state.isLoading)
        assertTrue(state.teams.isEmpty())
    }

    @Test
    fun `teams are loaded after initialization`() = runTest {
        val celtics = team(id = 1, name = "Celtics")
        val lakers = team(id = 2, name = "Lakers")
        networkClient.enqueue("v1/teams", teamsResponse(celtics, lakers))

        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals(listOf(celtics, lakers), vm.uiState.value.teams)
    }

    @Test
    fun `loading becomes false after teams load`() = runTest {
        networkClient.enqueue("v1/teams", teamsResponse())

        val vm = createViewModel()
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `favorite team ids observed from storage`() = runTest {
        networkClient.enqueue("v1/teams", teamsResponse())
        storageClient.setFavorites(setOf(1, 5))

        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals(setOf(1, 5), vm.uiState.value.favoriteTeamIds)
    }

    @Test
    fun `favorite ids update on new emissions`() = runTest {
        networkClient.enqueue("v1/teams", teamsResponse())

        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals(emptySet<Int>(), vm.uiState.value.favoriteTeamIds)

        storageClient.setFavorites(setOf(3))
        advanceUntilIdle()

        assertEquals(setOf(3), vm.uiState.value.favoriteTeamIds)
    }

    @Test
    fun `selectConference updates state`() = runTest {
        networkClient.enqueue("v1/teams", teamsResponse())

        val vm = createViewModel()
        advanceUntilIdle()

        vm.selectConference(Conference.EASTERN)

        assertEquals(Conference.EASTERN, vm.uiState.value.selectedConference)
    }

    @Test
    fun `selectConference cycles all conferences`() = runTest {
        networkClient.enqueue("v1/teams", teamsResponse())

        val vm = createViewModel()
        advanceUntilIdle()

        vm.selectConference(Conference.EASTERN)
        assertEquals(Conference.EASTERN, vm.uiState.value.selectedConference)

        vm.selectConference(Conference.WESTERN)
        assertEquals(Conference.WESTERN, vm.uiState.value.selectedConference)

        vm.selectConference(Conference.ALL)
        assertEquals(Conference.ALL, vm.uiState.value.selectedConference)
    }

    @Test
    fun `uiState emits loading then loaded`() = runTest {
        val celtics = team(id = 1, name = "Celtics")
        networkClient.enqueue("v1/teams", teamsResponse(celtics))

        val vm = createViewModel()

        vm.uiState.test {
            val loading = awaitItem()
            assertTrue(loading.isLoading)
            assertTrue(loading.teams.isEmpty())

            advanceUntilIdle()

            val loaded = awaitItem()
            assertFalse(loaded.isLoading)
            assertEquals(listOf(celtics), loaded.teams)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState reflects favorites update`() = runTest {
        networkClient.enqueue("v1/teams", teamsResponse())

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
    fun `default conference is ALL`() {
        networkClient.enqueue("v1/teams", teamsResponse())

        val vm = createViewModel()

        assertEquals(Conference.ALL, vm.uiState.value.selectedConference)
    }

    @Test
    fun `default favorites is empty set`() {
        networkClient.enqueue("v1/teams", teamsResponse())

        val vm = createViewModel()

        assertEquals(emptySet<Int>(), vm.uiState.value.favoriteTeamIds)
    }
}
