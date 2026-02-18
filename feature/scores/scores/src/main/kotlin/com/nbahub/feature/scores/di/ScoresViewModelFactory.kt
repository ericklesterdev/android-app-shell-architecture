package com.nbahub.feature.scores.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nbahub.feature.scores.data.ScoresService
import com.nbahub.feature.scores.ui.ScoresListViewModel
import com.nbahub.platform.storage.StorageClient
import javax.inject.Inject

internal class ScoresViewModelFactory @Inject constructor(
    private val scoresService: ScoresService,
    private val storageClient: StorageClient,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        ScoresListViewModel::class.java -> ScoresListViewModel(scoresService, storageClient)
        else -> throw IllegalArgumentException("Unknown ViewModel: $modelClass")
    } as T
}
