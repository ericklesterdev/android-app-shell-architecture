package com.nbahub.feature.scores.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider

internal val LocalScoresViewModelFactory = staticCompositionLocalOf<ViewModelProvider.Factory> {
    error("ScoresViewModelFactory not provided")
}
