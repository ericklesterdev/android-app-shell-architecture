plugins {
    id("nbahub.android.library.compose")
}

group = "com.example"

android {
    namespace = "com.nbahub.platform.design"
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}
