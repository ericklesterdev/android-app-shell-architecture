package com.nbahub.platform.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nbahub_prefs")

class DataStoreStorageClient(private val context: Context) : StorageClient {

    private val favoriteTeamIdsKey = stringSetPreferencesKey("favorite_team_ids")

    override fun observeFavoriteTeamIds(): Flow<Set<Int>> =
        context.dataStore.data.map { prefs ->
            prefs[favoriteTeamIdsKey]?.map { it.toInt() }?.toSet() ?: emptySet()
        }

    override suspend fun toggleFavoriteTeam(id: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[favoriteTeamIdsKey] ?: emptySet()
            val idString = id.toString()
            prefs[favoriteTeamIdsKey] = if (idString in current) {
                current - idString
            } else {
                current + idString
            }
        }
    }

    override suspend fun isFavoriteTeam(id: Int): Boolean =
        observeFavoriteTeamIds().first().contains(id)
}
