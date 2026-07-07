pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

includeBuild("build-logic")
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "OrienteeringApp"
include(":app")
include(":feature:login")
include(":technical:mobile")
include(":common:ui")
include(":technical:backend")
include(":common:core")
include(":common:loader")
include(":common:activityconnector")
include(":common:storage")
include(":common:network")
include(":common:security")
include(":common:validators")
include(":technical:user")
include(":feature:dashboard")
include(":feature:maps")
