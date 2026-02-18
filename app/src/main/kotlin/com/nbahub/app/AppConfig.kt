package com.nbahub.app

data class AppConfig(
    val useMockNetwork: Boolean = true,
    val apiKey: String = "",
    val baseUrl: String = "https://api.balldontlie.io/",
)
