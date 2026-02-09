plugins {
    id("nbahub.android.library")
}

group = "com.example"

android {
    namespace = "com.nbahub.platform.network"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coroutines.android)
}
