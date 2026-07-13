package pl.dev.bkwiatkowski.feature.maps.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import pl.dev.bkwiatkowski.common.core.navigation.AppNavController
import pl.dev.bkwiatkowski.common.core.navigation.createDestination
import pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap.EventsMapScreen
import pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap.EventsMapVM
import pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap.EventsMapVMImpl

fun NavGraphBuilder.mapsNavGraph(
  navController: AppNavController,
  onResult: (MapsResult) -> Unit,
) {
  navigation(
    route = MapsDestination.MapsGraph.route,
    startDestination = MapsDestination.EventsMap.route,
  ) {
    createDestination<Nothing, MapsContractVM, EventsMapVMImpl, EventsMapVM.Action.Navigation>(
      destination = MapsDestination.EventsMap,
      navController = navController,
      content = { viewModel ->
        EventsMapScreen(viewModel = viewModel)
      },
      navActionHandler = { action, contractViewModel ->
        when (action) {
          is EventsMapVM.Action.Navigation.Back -> onResult(MapsResult.Back)
          is EventsMapVM.Action.Navigation.ToEventDetails -> {}
        }
      }
    )
  }

}