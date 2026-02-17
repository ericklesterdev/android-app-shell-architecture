package com.nbahub.feature.teams.fake

import com.nbahub.platform.storage.StorageClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class FakeStorageClient : StorageClient {

    private val _favoriteTeamIds = MutableStateFlow<Set<Int>>(emptySet())

    override fun observeFavoriteTeamIds(): Flow<Set<Int>> = _favoriteTeamIds.asStateFlow()

    override suspend fun toggleFavoriteTeam(id: Int) {
        _favoriteTeamIds.value = if (id in _favoriteTeamIds.value) {
            _favoriteTeamIds.value - id
        } else {
            _favoriteTeamIds.value + id
        }
    }

    override suspend fun isFavoriteTeam(id: Int): Boolean = id in _favoriteTeamIds.value

    fun setFavorites(ids: Set<Int>) {
        _favoriteTeamIds.value = ids
    }
}
