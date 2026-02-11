package com.nbahub.app.ui.theme

import androidx.compose.runtime.Composable
import com.nbahub.platform.design.NbaHubTheme as SharedNbaHubTheme

@Composable
fun NbaHubTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    SharedNbaHubTheme(darkTheme = darkTheme, content = content)
}
