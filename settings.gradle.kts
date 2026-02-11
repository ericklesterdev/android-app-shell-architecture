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
includeBuild("platform/design") {
    dependencySubstitution {
        substitute(module("com.example:platform-design")).using(project(":"))
    }
}
includeBuild("platform/network") {
    dependencySubstitution {
        substitute(module("com.example:platform-network")).using(project(":"))
    }
}
includeBuild("platform/storage") {
    dependencySubstitution {
        substitute(module("com.example:platform-storage")).using(project(":"))
    }
}

// Feature modules
includeBuild("feature/teams") {
    dependencySubstitution {
        substitute(module("com.example:feature-teams")).using(project(":teams"))
    }
}
