package com.nbahub.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nbahub.platform.storage.StorageClient
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NbaHubViewModel(
    private val storageClient: StorageClient,
) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean> = storageClient.observeDarkTheme()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun toggleTheme() {
        viewModelScope.launch {
            storageClient.setDarkTheme(!isDarkTheme.value)
        }
    }

    companion object {
        fun factory(storageClient: StorageClient) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                NbaHubViewModel(storageClient) as T
        }
    }
}
