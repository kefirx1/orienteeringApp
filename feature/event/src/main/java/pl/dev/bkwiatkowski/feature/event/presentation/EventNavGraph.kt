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
import pl.dev.bkwiatkowski.feature.event.presentation.success.SuccessEventScreen
import pl.dev.bkwiatkowski.feature.event.presentation.success.SuccessEventVM
import pl.dev.bkwiatkowski.feature.event.presentation.success.SuccessEventVMImpl

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
          is EventMainVM.Action.Navigation.Completed -> {
            contractViewModel.setContractData(
              destination = EventDestination.Success,
              data = SuccessEventVM.SetupData(
                finishSessionResponse = action.response,
              ),
            )
            navController.navigate(
              destination = EventDestination.Success,
            )
          }
        }
      }
    )

    createDestination<SuccessEventVM.SetupData, EventContractVM, SuccessEventVMImpl, SuccessEventVM.Action.Navigation>(
      destination = EventDestination.Success,
      graphInitContract = appContractVM,
      navController = navController,
      content = { viewModel ->
        SuccessEventScreen(viewModel = viewModel)
      },
      navActionHandler = { action, contractViewModel ->
        when (action) {
          is SuccessEventVM.Action.Navigation.Back -> onResult(EventResult.Back)
        }
      }
    )
  }
}
