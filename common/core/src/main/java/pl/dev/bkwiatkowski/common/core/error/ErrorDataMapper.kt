package pl.dev.bkwiatkowski.common.core.error

import pl.dev.bkwiatkowski.common.core.usecase.Mapper

interface ErrorDataMapper : Mapper<ErrorDataMapper.Params, ErrorScreenData> {
  data class Params(
    val error: DomainError,
    val customMessage: String? = null,
    val onCloseClick: () -> Unit,
    val onRetryClick: (() -> Unit)? = null,
  )
}