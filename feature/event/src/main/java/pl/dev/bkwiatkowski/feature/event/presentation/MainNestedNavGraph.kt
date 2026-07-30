package pl.dev.bkwiatkowski.feature.event.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import pl.dev.bkwiatkowski.common.core.navigation.AppNavController
import pl.dev.bkwiatkowski.common.core.navigation.createDestination
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameScreen
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameVM
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameVMImpl
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainVM
import pl.dev.bkwiatkowski.feature.event.presentation.map.EventMapScreen
import pl.dev.bkwiatkowski.feature.event.presentation.map.EventMapVM
import pl.dev.bkwiatkowski.feature.event.presentation.map.EventMapVMImpl

@Composable
fun MainNestedNavGraph(
  mainVM: EventMainVM,
) {
  val navController = AppNavController(
    navController = rememberNavController(),
  )

  LaunchedEffect(Unit) {
    mainVM.nestedNavAction.collect { action ->
      when (action) {
        is EventMainVM.Action.NestedNavigation.GoToMap -> navController.navigate(
          destination = EventDestination.EventMap,
        )
        is EventMainVM.Action.NestedNavigation.GoToGame -> navController.navigate(
          destination = EventDestination.EventGame,
        )
      }

    }
  }

  NavHost(
    navController = navController.navController,
    startDestination = EventDestination.EventMap.route,
  ) {
    createDestination<Nothing, EventContractVM, EventMapVMImpl, EventMapVM.Action.Navigation>(
      destination = EventDestination.EventMap,
      navController = navController,
      content = { viewModel -> EventMapScreen(viewModel = viewModel) },
      navActionHandler = { action, contractViewModel ->
        when (action) {
          is EventMapVM.Action.Navigation.Back -> mainVM.onBackClick()
        }
      }
    )

    createDestination<Nothing, EventContractVM, EventGameVMImpl, EventGameVM.Action.Navigation>(
      destination = EventDestination.EventGame,
      navController = navController,
      content = { viewModel -> EventGameScreen(viewModel = viewModel) },
      navActionHandler = { action, contractViewModel ->
        when (action) {
          is EventGameVM.Action.Navigation.Back -> mainVM.onMapClick()
        }
      }
    )
  }
}