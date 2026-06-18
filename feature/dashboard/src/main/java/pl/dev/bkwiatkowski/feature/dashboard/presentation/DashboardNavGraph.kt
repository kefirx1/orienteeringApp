package pl.dev.bkwiatkowski.feature.dashboard.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import pl.dev.bkwiatkowski.common.core.navigation.AppNavController
import pl.dev.bkwiatkowski.common.core.navigation.createDestination
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.MainDashboardScreen
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.MainDashboardVM
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.MainDashboardVMImpl

fun NavGraphBuilder.dashboardNavGraph(
  navController: AppNavController,
  onResult: (DashboardResult) -> Unit,
) {
  navigation(
    route = DashboardDestination.DashboardGraph.route,
    startDestination = DashboardDestination.MainDashboard.route,
  ) {
    createDestination<Nothing, DashboardContractVM, MainDashboardVMImpl, MainDashboardVM.Action.Navigation>(
      destination = DashboardDestination.MainDashboard,
      navController = navController,
      content = { viewModel ->
        MainDashboardScreen(viewModel = viewModel)
      },
      navActionHandler = { action, contractViewModel ->
        when (action) {
          is MainDashboardVM.Action.Navigation.ExitApp -> onResult(DashboardResult.ExitApp)
        }
      }
    )
  }
}