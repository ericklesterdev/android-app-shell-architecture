package com.nbahub.feature.teams.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nbahub.feature.teams.LocalTeamsDependencies
import com.nbahub.feature.teams.TeamsFeatureDependencies

@Composable
internal inline fun <reified VM : ViewModel> provideViewModel(
    key: String? = null,
    crossinline factory: (TeamsFeatureDependencies) -> ViewModelProvider.Factory,
): VM {
    val deps = LocalTeamsDependencies.current
    return viewModel(key = key, factory = factory(deps))
}
