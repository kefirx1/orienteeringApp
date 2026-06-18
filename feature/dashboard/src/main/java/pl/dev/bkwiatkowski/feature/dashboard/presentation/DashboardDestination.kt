package pl.dev.bkwiatkowski.feature.dashboard.presentation

import kotlinx.serialization.Serializable
import pl.dev.bkwiatkowski.common.core.navigation.Destination

sealed interface DashboardDestination : Destination {

  @Serializable
  data object DashboardGraph : Destination

  @Serializable
  data object MainDashboard : DashboardDestination
}

sealed interface DashboardResult {
  data object ExitApp : DashboardResult
}