package pl.dev.bkwiatkowski.feature.dashboard.presentation.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.component.dialog.DialogScreen

@Composable
fun DashboardDialogScreen(viewModel: DashboardDialogVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  DialogScreen(dialogData = state.dialogData)
}