package com.nbahub.platform.contracts

import kotlinx.coroutines.flow.Flow

interface StorageClient {
    fun observeFavoriteTeamIds(): Flow<Set<Int>>
    suspend fun toggleFavoriteTeam(id: Int)
    suspend fun isFavoriteTeam(id: Int): Boolean
}
