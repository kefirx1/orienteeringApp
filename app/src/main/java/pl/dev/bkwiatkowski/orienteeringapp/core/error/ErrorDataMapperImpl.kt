package pl.dev.bkwiatkowski.orienteeringapp.core.error

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.error.ErrorDataMapper
import pl.dev.bkwiatkowski.common.core.error.ErrorScreenData

class ErrorDataMapperImpl : ErrorDataMapper {
  override fun invoke(params: ErrorDataMapper.Params): ErrorScreenData {
    when (val error = params.error) {
      is DomainError.Custom -> {
        return ErrorScreenData(
          customMessage = params.customMessage,
          onCloseButtonClick = params.onCloseClick,
          onRetryButtonClick = params.onRetryClick,
        )
      }
      is DomainError.NoNetwork -> {
        return ErrorScreenData(
          customMessage = params.customMessage ?: "Błąd połączenia z siecią",
          onCloseButtonClick = params.onCloseClick,
          onRetryButtonClick = params.onRetryClick,
        )
      }
      is DomainError.Network -> {
        return ErrorScreenData(
          customMessage = params.customMessage ?: error.message,
          onCloseButtonClick = params.onCloseClick,
          onRetryButtonClick = params.onRetryClick,
        )
      }
    }
  }
}