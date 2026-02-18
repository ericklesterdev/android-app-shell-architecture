pluginManagement {
    includeBuild("../../../build-logic/convention")
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

rootProject.name = "nba-scores-sdk-showcase"

// Simulate external SDK consumption via Maven coordinates
includeBuild("..") {
    dependencySubstitution {
        substitute(module("com.example:nba-scores-sdk")).using(project(":nba-scores-sdk"))
    }
}
