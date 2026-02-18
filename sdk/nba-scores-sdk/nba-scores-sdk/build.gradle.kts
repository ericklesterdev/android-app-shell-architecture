plugins {
    id("nbahub.android.library.compose")
}

group = "com.example"

android {
    namespace = "com.nbahub.sdk.scores"
}

// AGP lint cannot resolve dependencies from composite builds (includeBuild).
tasks.configureEach {
    if (name.contains("lint", ignoreCase = true)) {
        enabled = false
    }
}

dependencies {
    api("com.example:feature-scores")
    implementation("com.example:platform-network")
    implementation("com.example:platform-storage")

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.coroutines.android)
    implementation(libs.lifecycle.runtime.compose)
}
