package pl.dev.bkwiatkowski.technical.user.domain.interactor

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either

interface UserEventInteractor {
  suspend fun clearData(): Either<DomainError, Unit>
}