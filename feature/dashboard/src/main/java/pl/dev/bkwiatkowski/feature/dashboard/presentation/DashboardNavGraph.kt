package pl.dev.bkwiatkowski.feature.dashboard.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import pl.dev.bkwiatkowski.common.core.navigation.AppNavController
import pl.dev.bkwiatkowski.common.core.navigation.DestinationType
import pl.dev.bkwiatkowski.common.core.navigation.createDestination
import pl.dev.bkwiatkowski.common.ui.component.dialog.DialogData
import pl.dev.bkwiatkowski.feature.dashboard.presentation.changepassword.ChangePasswordScreen
import pl.dev.bkwiatkowski.feature.dashboard.presentation.changepassword.ChangePasswordVM
import pl.dev.bkwiatkowski.feature.dashboard.presentation.changepassword.ChangePasswordVMImpl
import pl.dev.bkwiatkowski.feature.dashboard.presentation.dialog.DashboardDialogScreen
import pl.dev.bkwiatkowski.feature.dashboard.presentation.dialog.DashboardDialogVM
import pl.dev.bkwiatkowski.feature.dashboard.presentation.dialog.DashboardDialogVMImpl
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
          is MainDashboardVM.Action.Navigation.GoToMap -> onResult(DashboardResult.ToMaps)
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
          is SettingsDashboardVM.Action.Navigation.OpenLogoutDialog -> {
            contractViewModel.setContractData(
              destination = DashboardDestination.DashboardDialog,
              data = action.dialogData,
            )

            navController.navigate(destination = DashboardDestination.DashboardDialog)
          }

          is SettingsDashboardVM.Action.Navigation.Logout -> onResult(DashboardResult.Logout)
          is SettingsDashboardVM.Action.Navigation.OpenChangePassword -> navController.navigate(
            destination = DashboardDestination.ChangePassword,
          )
        }
      }
    )

    createDestination<Nothing, DashboardContractVM, ChangePasswordVMImpl, ChangePasswordVM.Action.Navigation>(
      destination = DashboardDestination.ChangePassword,
      navController = navController,
      content = { viewModel ->
        ChangePasswordScreen(viewModel = viewModel)
      },
      navActionHandler = { action, contractViewModel ->
        when (action) {
          is ChangePasswordVM.Action.Navigation.Back -> navController.popBackStack()
          is ChangePasswordVM.Action.Navigation.Logout -> onResult(DashboardResult.Logout)
        }
      }
    )

    createDestination<DialogData, DashboardContractVM, DashboardDialogVMImpl, DashboardDialogVM.Action.Navigation>(
      destination = DashboardDestination.DashboardDialog,
      destinationType = DestinationType.Dialog,
      navController = navController,
      content = { viewModel ->
        DashboardDialogScreen(viewModel = viewModel)
      },
      navActionHandler = { action, contractViewModel ->
        when (action) {
          is DashboardDialogVM.Action.Navigation.OnDialogAction -> {
            navController.popBackStack()
            action.dialogAction()
          }
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