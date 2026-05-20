package pl.dev.bkwiatkowski.feature.login.presentation.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.component.dialog.DialogScreen

@Composable
fun AuthDialogScreen(viewModel: AuthDialogVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  DialogScreen(dialogData = state.dialogData)
}