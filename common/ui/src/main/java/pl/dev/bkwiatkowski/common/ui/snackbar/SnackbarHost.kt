package pl.dev.bkwiatkowski.common.ui.snackbar

import androidx.compose.material3.SnackbarHostState

interface SnackbarHost {
  val snackbarHost: SnackbarHostState

  suspend fun showSnackbar(message: String)
}

class SnackbarHostImpl: SnackbarHost {
  override val snackbarHost: SnackbarHostState = SnackbarHostState()

  override suspend fun showSnackbar(message: String) {
    snackbarHost.showSnackbar(message = message)
  }
}