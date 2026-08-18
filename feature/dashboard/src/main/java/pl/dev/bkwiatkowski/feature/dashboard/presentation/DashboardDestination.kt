package pl.dev.bkwiatkowski.feature.dashboard.presentation

import kotlinx.serialization.Serializable
import pl.dev.bkwiatkowski.common.core.navigation.Destination

sealed interface DashboardDestination : Destination {

  @Serializable
  data object DashboardGraph : Destination

  @Serializable
  data object MainDashboard : DashboardDestination

  @Serializable
  data object Settings : DashboardDestination

  @Serializable
  data object ChangePassword : DashboardDestination

  @Serializable
  data object UserProfile : DashboardDestination

  @Serializable
  data object DashboardDialog : DashboardDestination
}

sealed interface DashboardResult {
  data object ExitApp : DashboardResult
  data object ToMaps : DashboardResult
  data object Logout : DashboardResult
}