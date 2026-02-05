plugins {
    id("nbahub.kotlin.library")
}

group = "com.example"

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
}
