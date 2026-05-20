package pl.dev.bkwiatkowski.feature.login.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import pl.dev.bkwiatkowski.common.core.navigation.AppNavController
import pl.dev.bkwiatkowski.common.core.navigation.createDestination
import pl.dev.bkwiatkowski.feature.login.presentation.login.LoginScreen
import pl.dev.bkwiatkowski.feature.login.presentation.login.LoginVM
import pl.dev.bkwiatkowski.feature.login.presentation.login.LoginVMImpl

fun NavGraphBuilder.loginNavGraph(
  navController: AppNavController,
  onResult: (LoginResult) -> Unit,
) {

  navigation(
    route = LoginDestinations.LoginGraph.route,
    startDestination = LoginDestinations.Login.route,
  ) {
    createDestination<Nothing, LoginContractVM, LoginVMImpl, LoginVM.Action.Navigation>(
      destination = LoginDestinations.Login,
      navController = navController,
      content = { viewModel ->
        LoginScreen(viewModel = viewModel)
      },
      navActionHandler = { action, contractViewModel ->
        when (action) {
          is LoginVM.Action.Navigation.ToDashboard -> onResult(LoginResult.LoginSuccess)
          is LoginVM.Action.Navigation.ToOnboarding -> {} //TODO
          is LoginVM.Action.Navigation.Back -> onResult(LoginResult.ExitApp)
        }
      },
    )
  }
}