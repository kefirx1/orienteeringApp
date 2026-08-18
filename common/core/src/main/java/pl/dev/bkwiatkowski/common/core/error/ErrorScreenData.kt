package pl.dev.bkwiatkowski.common.core.error

data class ErrorScreenData(
  val customMessage: String?,
  val onCloseButtonClick: () -> Unit,
  val onRetryButtonClick: (() -> Unit)?,
)
