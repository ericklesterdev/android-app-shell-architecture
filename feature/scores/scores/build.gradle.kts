plugins {
    id("nbahub.android.library.compose")
}

apply(plugin = "com.google.devtools.ksp")

group = "com.example"

android {
    namespace = "com.nbahub.feature.scores"
}

// AGP lint cannot resolve dependencies from composite builds (includeBuild).
tasks.configureEach {
    if (name.contains("lint", ignoreCase = true)) {
        enabled = false
    }
}

dependencies {
    api("com.example:platform-design")
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

    implementation(libs.dagger)
    add("ksp", libs.dagger.compiler)

    debugImplementation(libs.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
}
