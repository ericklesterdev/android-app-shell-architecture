package com.example.externalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import com.nbahub.sdk.scores.LiveScoresWidget
import com.nbahub.sdk.scores.NbaEnvironment
import com.nbahub.sdk.scores.NbaSDK
import com.nbahub.sdk.scores.NbaSDKConfig

class ExternalAppActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NbaSDK.initialize(
            NbaSDKConfig(
                context = applicationContext,
                apiKey = "",
                environment = NbaEnvironment.PRODUCTION,
                useMockData = true,
            ),
        )

        setContent {
            MaterialTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("External App") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                        )
                    },
                ) { innerPadding ->
                    LiveScoresWidget(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
