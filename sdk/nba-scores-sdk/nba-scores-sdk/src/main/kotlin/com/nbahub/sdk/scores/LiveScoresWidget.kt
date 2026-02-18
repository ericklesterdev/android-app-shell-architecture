package com.nbahub.sdk.scores

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nbahub.feature.scores.ScoresScreen
import com.nbahub.platform.design.NbaHubTheme

@Composable
fun LiveScoresWidget(modifier: Modifier = Modifier) {
    NbaHubTheme {
        ScoresScreen(
            dependencies = NbaSDK.container.scoresFeatureDependencies,
            modifier = modifier,
        )
    }
}
