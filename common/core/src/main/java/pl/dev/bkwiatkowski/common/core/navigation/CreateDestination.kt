package pl.dev.bkwiatkowski.common.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModelFactory
import pl.dev.bkwiatkowski.common.core.viewmodel.ContractViewModel

sealed interface DestinationType {
  data object Screen : DestinationType
  data object Dialog : DestinationType
}

@Composable
inline fun <reified CVM: ContractViewModel> rememberContractViewModel(navController: AppNavController): CVM {
  val currentBackStackEntry = navController.navController.currentBackStackEntry
  val parentGraphRoute = currentBackStackEntry?.destination?.parent?.route ?: return hiltViewModel()

  val parentEntry = remember(currentBackStackEntry) {
    navController.navController.getBackStackEntry(parentGraphRoute)
  }

  return hiltViewModel(parentEntry)
}

inline fun <CONTRACT: Any?, reified CVM: ContractViewModel, reified VM: CustomViewModel<*, *, NAV>, NAV> NavGraphBuilder.createDestination(
  destination: Destination,
  destinationType: DestinationType = DestinationType.Screen,
  navController: AppNavController,
  graphInitContract: ContractViewModel? = null,
  noinline navActionHandler: (NAV, ContractViewModel) -> Unit = { _, _ -> },
  crossinline content: @Composable (VM) -> Unit
) {

  when (destinationType) {
    DestinationType.Screen -> composable(route = destination.route) {
      val sharedViewModel = rememberContractViewModel<CVM>(
        navController = navController,
      )
      graphInitContract?.let { contract ->
        contract.retrieveData<CONTRACT>(destination = destination)?.let { initData ->
          sharedViewModel.setContractData(
            destination = destination,
            data = initData,
          )
        }
      }
      val setupData = sharedViewModel.retrieveData<CONTRACT>(destination = destination)

      val viewModel = if (setupData != null) {
        hiltViewModel(
          creationCallback = { factory: CustomViewModelFactory<CONTRACT, VM> ->
            factory.setup(setupData = setupData)
          }
        )
      } else { hiltViewModel<VM>() }

      NavActionHandler(
        viewModel = viewModel,
        handler = navActionHandler,
        sharedViewModel = sharedViewModel
      )
      content(viewModel)
    }
    DestinationType.Dialog -> dialog(route = destination.route) {
      val sharedViewModel = rememberContractViewModel<CVM>(
        navController = navController,
      )
      graphInitContract?.let { contract ->
        contract.retrieveData<CONTRACT>(destination = destination)?.let { initData ->
          sharedViewModel.setContractData(
            destination = destination,
            data = initData,
          )
        }
      }
      val setupData = sharedViewModel.retrieveData<CONTRACT>(destination = destination)

      val viewModel = if (setupData != null) {
        hiltViewModel(
          creationCallback = { factory: CustomViewModelFactory<CONTRACT, VM> ->
            factory.setup(setupData = setupData)
          }
        )
      } else { hiltViewModel<VM>() }
      NavActionHandler(
        viewModel = viewModel,
        handler = navActionHandler,
        sharedViewModel = sharedViewModel
      )
      content(viewModel)
    }
  }

}
