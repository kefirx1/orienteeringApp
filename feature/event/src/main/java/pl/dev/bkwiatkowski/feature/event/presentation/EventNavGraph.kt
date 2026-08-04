package pl.dev.bkwiatkowski.feature.event.presentation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import pl.dev.bkwiatkowski.common.core.navigation.AppNavController
import pl.dev.bkwiatkowski.common.core.navigation.createDestination
import pl.dev.bkwiatkowski.common.core.viewmodel.ContractViewModel
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainScreen
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainVM
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainVMImpl

fun NavGraphBuilder.eventNavGraph(
  appContractVM: ContractViewModel,
  navController: AppNavController,
  onResult: (EventResult) -> Unit,
) {
  navigation(
    route = EventDestination.EventGraph.route,
    startDestination = EventDestination.EventMain.route,
  ) {
    createDestination<EventMainVM.SetupData, EventContractVM, EventMainVMImpl, EventMainVM.Action.Navigation>(
      graphInitContract = appContractVM,
      destination = EventDestination.EventMain,
      navController = navController,
      content = { viewModel ->
        val shared = hiltViewModel<EventSharedVM>()
        viewModel.setupContract(contract = shared)

        EventMainScreen(
          viewModel = viewModel,
          nestedContent = {
            MainNestedNavGraph(
              mainVM = viewModel,
              shared = shared,
            )
          },
        )
      },
      navActionHandler = { action, contractViewModel ->
        when (action) {
          is EventMainVM.Action.Navigation.Back -> onResult(EventResult.Back)
        }
      }
    )
  }
}
