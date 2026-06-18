package pl.dev.bkwiatkowski.feature.login.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import pl.dev.bkwiatkowski.common.core.navigation.AppNavController
import pl.dev.bkwiatkowski.common.core.navigation.createDestination
import pl.dev.bkwiatkowski.feature.login.presentation.login.LoginScreen
import pl.dev.bkwiatkowski.feature.login.presentation.login.LoginVM
import pl.dev.bkwiatkowski.feature.login.presentation.login.LoginVMImpl
import pl.dev.bkwiatkowski.feature.login.presentation.onboarding.OnboardingScreen
import pl.dev.bkwiatkowski.feature.login.presentation.onboarding.OnboardingVM
import pl.dev.bkwiatkowski.feature.login.presentation.onboarding.OnboardingVMImpl
import pl.dev.bkwiatkowski.feature.login.presentation.setpassword.SetPasswordScreen
import pl.dev.bkwiatkowski.feature.login.presentation.setpassword.SetPasswordVM
import pl.dev.bkwiatkowski.feature.login.presentation.setpassword.SetPasswordVMImpl

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
          is LoginVM.Action.Navigation.ToOnboarding -> navController.navigate(destination = LoginDestinations.Onboarding)
          is LoginVM.Action.Navigation.Back -> onResult(LoginResult.ExitApp)
        }
      },
    )

    createDestination<Nothing, LoginContractVM, OnboardingVMImpl, OnboardingVM.Action.Navigation>(
      destination = LoginDestinations.Onboarding,
      navController = navController,
      content = { viewModel ->
        OnboardingScreen(viewModel = viewModel)
      },
      navActionHandler = { action, contractViewModel ->
        when (action) {
          is OnboardingVM.Action.Navigation.Back -> navController.popBackStack()
          is OnboardingVM.Action.Navigation.ContinueOnboarding -> {
            contractViewModel.setContractData(
              destination = LoginDestinations.SetPassword,
              data = action.setupData,
            )
            navController.navigate(destination = LoginDestinations.SetPassword)
          }
        }
      },
    )

    createDestination<SetPasswordVM.SetupData, LoginContractVM, SetPasswordVMImpl, SetPasswordVM.Action.Navigation>(
      destination = LoginDestinations.SetPassword,
      navController = navController,
      content = { viewModel ->
        SetPasswordScreen(viewModel = viewModel)
      },
      navActionHandler = { action, contractViewModel ->
        when (action) {
          is SetPasswordVM.Action.Navigation.Back -> navController.popBackStack()
          is SetPasswordVM.Action.Navigation.RegistrationSuccess -> onResult(LoginResult.LoginSuccess)
        }
      },
    )
  }
}