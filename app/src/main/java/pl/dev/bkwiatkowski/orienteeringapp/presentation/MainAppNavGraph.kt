package pl.dev.bkwiatkowski.orienteeringapp.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import pl.dev.bkwiatkowski.common.core.navigation.AppNavController
import pl.dev.bkwiatkowski.feature.dashboard.presentation.DashboardDestination
import pl.dev.bkwiatkowski.feature.dashboard.presentation.DashboardResult
import pl.dev.bkwiatkowski.feature.dashboard.presentation.dashboardNavGraph
import pl.dev.bkwiatkowski.feature.event.presentation.EventDestination
import pl.dev.bkwiatkowski.feature.event.presentation.EventResult
import pl.dev.bkwiatkowski.feature.event.presentation.eventNavGraph
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainVM
import pl.dev.bkwiatkowski.feature.login.presentation.LoginDestinations
import pl.dev.bkwiatkowski.feature.login.presentation.LoginResult
import pl.dev.bkwiatkowski.feature.login.presentation.loginNavGraph
import pl.dev.bkwiatkowski.feature.maps.presentation.MapsDestination
import pl.dev.bkwiatkowski.feature.maps.presentation.MapsResult
import pl.dev.bkwiatkowski.feature.maps.presentation.mapsNavGraph

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
          LoginResult.LoginSuccess ->
            appNavController.navigate(destination = DashboardDestination.DashboardGraph)
          LoginResult.ExitApp -> onAppExit()
        }
      },
    )
    dashboardNavGraph(
      navController = appNavController,
      onResult = { result ->
        when (result) {
          DashboardResult.ExitApp -> onAppExit()
          DashboardResult.ToMaps -> appNavController.navigate(destination = MapsDestination.MapsGraph)
          DashboardResult.Logout -> appNavController.navigate(destination = LoginDestinations.LoginGraph)
        }
      },
    )
    mapsNavGraph(
      navController = appNavController,
      onResult = { result ->
        when (result) {
          MapsResult.Back -> appNavController.popBackStack()
          is MapsResult.ToEventSession -> {
            appContractVM.setContractData(
              destination = EventDestination.EventMain,
              data = EventMainVM.SetupData(
                sessionUuid = result.sessionUuid,
                eventId = result.eventId,
              ),
            )
            appNavController.navigate(
              destination = EventDestination.EventGraph,
            )
          }
        }
      },
    )
    eventNavGraph(
      appContractVM = appContractVM,
      navController = appNavController,
      onResult = { result ->
        when (result) {
          is EventResult.Back -> appNavController.navigate(
            destination = MapsDestination.EventDetails,
          )
        }
      },
    )
  }
}