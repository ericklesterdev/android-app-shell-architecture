plugins {
    id("nbahub.android.application.compose")
}

android {
    namespace = "com.nbahub.app"

    defaultConfig {
        applicationId = "com.nbahub.app"
        versionCode = 1
        versionName = "1.0.0"
    }
}

dependencies {
    implementation("com.example:platform-design")
    implementation("com.example:platform-network")
    implementation("com.example:platform-storage")
    implementation("com.example:feature-teams")

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.runtime.compose)

    debugImplementation(libs.compose.ui.tooling)
}
