pluginManagement {
    includeBuild("build-logic/convention")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NbaHub"

include(":app")

// Platform modules
includeBuild("platform/contracts") {
    dependencySubstitution {
        substitute(module("com.example:platform-contracts")).using(project(":"))
    }
}
