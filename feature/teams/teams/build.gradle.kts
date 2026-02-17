plugins {
    id("nbahub.android.library.compose")
}

group = "com.example"

android {
    namespace = "com.nbahub.feature.teams"
}

// AGP lint cannot resolve dependencies from composite builds (includeBuild).
// Disable lint tasks for this module since it depends on platform modules via includeBuild.
tasks.configureEach {
    if (name.contains("lint", ignoreCase = true)) {
        enabled = false
    }
}

dependencies {
    implementation("com.example:platform-design")
    implementation("com.example:platform-network")
    implementation("com.example:platform-storage")

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coroutines.core)
    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)

    debugImplementation(libs.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
}
