package pl.dev.bkwiatkowski.orienteeringapp.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import pl.dev.bkwiatkowski.common.core.navigation.AppNavController
import pl.dev.bkwiatkowski.feature.login.presentation.LoginDestinations
import pl.dev.bkwiatkowski.feature.login.presentation.LoginResult
import pl.dev.bkwiatkowski.feature.login.presentation.loginNavGraph

@Composable
fun MainAppNavGraph(
  onAppExit: () -> Unit,
) {
  val appNavController = AppNavController(
    navController = rememberNavController(),
  )
  val appContractVM = hiltViewModel<AppContractVM>()

  NavHost(
    navController = appNavController.navController,
    startDestination = LoginDestinations.LoginGraph.route,
  ) {

    loginNavGraph(
      navController = appNavController,
      onResult = { result ->
        when (result) {
          LoginResult.LoginSuccess -> {}
          LoginResult.ExitApp -> onAppExit()
        }
      },
    )

  }
}