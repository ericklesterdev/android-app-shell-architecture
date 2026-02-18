plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.serialization.gradle.plugin)
    implementation(libs.compose.compiler.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("kotlinLibrary") {
            id = "nbahub.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
        register("androidLibrary") {
            id = "nbahub.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "nbahub.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidApplication") {
            id = "nbahub.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "nbahub.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("detekt") {
            id = "nbahub.detekt"
            implementationClass = "DetektConventionPlugin"
        }
    }
}
