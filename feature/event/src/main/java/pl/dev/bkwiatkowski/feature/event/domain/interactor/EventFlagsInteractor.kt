package pl.dev.bkwiatkowski.feature.event.domain.interactor

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either

interface EventFlagsInteractor {
  suspend fun isDebugLocationEnabled(): Either<DomainError, Boolean>
}
