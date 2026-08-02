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
  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
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
include(":common:localization")
include(":common:permissions")
include(":common:activityconnector")
include(":common:camera")
include(":common:storage")
include(":common:network")
include(":common:security")
include(":common:validators")
include(":common:time")
include(":technical:user")
include(":feature:dashboard")
include(":feature:maps")
include(":feature:event")
