package com.nbahub.platform.storage

import kotlinx.coroutines.flow.Flow

interface StorageClient {
    fun observeFavoriteTeamIds(): Flow<Set<Int>>
    suspend fun toggleFavoriteTeam(id: Int)
    suspend fun isFavoriteTeam(id: Int): Boolean
    fun observeDarkTheme(): Flow<Boolean>
    suspend fun setDarkTheme(enabled: Boolean)
}
