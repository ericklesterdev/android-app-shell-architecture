plugins {
    id("nbahub.android.application.compose")
}

android {
    namespace = "com.example.externalapp"

    defaultConfig {
        applicationId = "com.example.externalapp"
        versionCode = 1
        versionName = "1.0.0"
    }
}

// AGP lint cannot resolve dependencies from composite builds (includeBuild).
tasks.configureEach {
    if (name.contains("lint", ignoreCase = true)) {
        enabled = false
    }
}

dependencies {
    implementation(project(":nba-scores-sdk"))

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.compose)
}
