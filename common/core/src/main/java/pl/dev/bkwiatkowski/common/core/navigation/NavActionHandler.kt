package pl.dev.bkwiatkowski.common.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import pl.dev.bkwiatkowski.common.core.viewmodel.ContractViewModel
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel

@Composable
fun <NAV> NavActionHandler(viewModel: CustomViewModel<*, *, NAV>, handler: (NAV, ContractViewModel) -> Unit, sharedViewModel: ContractViewModel) {
  LaunchedEffect(viewModel.navAction) {
    viewModel.navAction.collect {  action ->
      handler(action, sharedViewModel)
    }
  }
}