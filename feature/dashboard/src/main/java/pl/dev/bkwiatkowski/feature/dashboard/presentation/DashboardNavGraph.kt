package pl.dev.bkwiatkowski.feature.dashboard.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import pl.dev.bkwiatkowski.common.core.navigation.AppNavController
import pl.dev.bkwiatkowski.common.core.navigation.createDestination
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.MainDashboardScreen
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.MainDashboardVM
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.MainDashboardVMImpl
import pl.dev.bkwiatkowski.feature.dashboard.presentation.settings.SettingsDashboardScreen
import pl.dev.bkwiatkowski.feature.dashboard.presentation.settings.SettingsDashboardVM
import pl.dev.bkwiatkowski.feature.dashboard.presentation.settings.SettingsDashboardVMImpl
import pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile.UserProfileDashboardScreen
import pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile.UserProfileDashboardVM
import pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile.UserProfileDashboardVMImpl

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
          is MainDashboardVM.Action.Navigation.GoToSettings -> navController.navigate(destination = DashboardDestination.Settings)
          is MainDashboardVM.Action.Navigation.GoToNewRuns -> {}
          is MainDashboardVM.Action.Navigation.GoToMyProfile -> navController.navigate(destination = DashboardDestination.UserProfile)
          is MainDashboardVM.Action.Navigation.GoToMap -> {}
        }
      }
    )

    createDestination<Nothing, DashboardContractVM, SettingsDashboardVMImpl, SettingsDashboardVM.Action.Navigation>(
      destination = DashboardDestination.Settings,
      navController = navController,
      content = { viewModel ->
        SettingsDashboardScreen(viewModel = viewModel)
      },
      navActionHandler = { action, contractViewModel ->
        when (action) {
          is SettingsDashboardVM.Action.Navigation.Back -> navController.popBackStack()
        }
      }
    )

    createDestination<Nothing, DashboardContractVM, UserProfileDashboardVMImpl, UserProfileDashboardVM.Action.Navigation>(
      destination = DashboardDestination.UserProfile,
      navController = navController,
      content = { viewModel ->
        UserProfileDashboardScreen(viewModel = viewModel)
      },
      navActionHandler = { action, contractViewModel ->
        when (action) {
          is UserProfileDashboardVM.Action.Navigation.Back -> navController.popBackStack()
        }
      }
    )
  }
}