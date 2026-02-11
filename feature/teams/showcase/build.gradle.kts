plugins {
    id("nbahub.android.application.compose")
}

android {
    namespace = "com.nbahub.feature.teams.showcase"

    defaultConfig {
        applicationId = "com.nbahub.feature.teams.showcase"
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
    implementation(project(":teams"))
    implementation("com.example:platform-network")
    implementation("com.example:platform-storage")

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.compose)
}
