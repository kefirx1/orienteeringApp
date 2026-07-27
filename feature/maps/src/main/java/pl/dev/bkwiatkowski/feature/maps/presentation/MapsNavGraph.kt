package pl.dev.bkwiatkowski.feature.maps.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import pl.dev.bkwiatkowski.common.core.navigation.AppNavController
import pl.dev.bkwiatkowski.common.core.navigation.createDestination
import pl.dev.bkwiatkowski.feature.maps.presentation.eventdetails.EventDetailsScreen
import pl.dev.bkwiatkowski.feature.maps.presentation.eventdetails.EventDetailsVM
import pl.dev.bkwiatkowski.feature.maps.presentation.eventdetails.EventDetailsVMImpl
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
          is EventsMapVM.Action.Navigation.ToEventDetails -> {
            contractViewModel.setContractData(
              destination = MapsDestination.EventDetails,
              data = EventDetailsVM.SetupData(eventId = action.eventId),
            )
            navController.navigate(destination = MapsDestination.EventDetails)
          }
        }
      }
    )

    createDestination<EventDetailsVM.SetupData, MapsContractVM, EventDetailsVMImpl, EventDetailsVM.Action.Navigation>(
      destination = MapsDestination.EventDetails,
      navController = navController,
      content = { viewModel ->
        EventDetailsScreen(viewModel = viewModel)
      },
      navActionHandler = { action, contractViewModel ->
        when (action) {
          is EventDetailsVM.Action.Navigation.Back -> navController.popBackStack()
          is EventDetailsVM.Action.Navigation.ToEventSession ->
            onResult(
              MapsResult.ToEventSession(
                sessionUuid = action.sessionUuid,
                eventId = action.eventId,
              )
            )
        }
      }
    )
  }

}