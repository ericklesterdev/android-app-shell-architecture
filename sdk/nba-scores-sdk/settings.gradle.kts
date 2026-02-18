pluginManagement {
    includeBuild("../../build-logic/convention")
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

rootProject.name = "nba-scores-sdk"

include(":nba-scores-sdk")

// Platform modules
includeBuild("../../platform/design") {
    dependencySubstitution {
        substitute(module("com.example:platform-design")).using(project(":"))
    }
}
includeBuild("../../platform/network") {
    dependencySubstitution {
        substitute(module("com.example:platform-network")).using(project(":"))
    }
}
includeBuild("../../platform/storage") {
    dependencySubstitution {
        substitute(module("com.example:platform-storage")).using(project(":"))
    }
}

// Feature modules
includeBuild("../../feature/scores") {
    dependencySubstitution {
        substitute(module("com.example:feature-scores")).using(project(":scores"))
    }
}
