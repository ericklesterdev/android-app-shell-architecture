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

rootProject.name = "feature-teams"

include(":teams")
include(":showcase")

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
