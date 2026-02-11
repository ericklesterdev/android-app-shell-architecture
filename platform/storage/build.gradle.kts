plugins {
    id("nbahub.android.library")
}

group = "com.example"

android {
    namespace = "com.nbahub.platform.storage"
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.datastore.preferences)
}
