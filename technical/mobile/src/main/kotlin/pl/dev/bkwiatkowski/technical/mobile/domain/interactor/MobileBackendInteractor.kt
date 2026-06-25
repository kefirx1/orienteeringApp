package pl.dev.bkwiatkowski.technical.mobile.domain.interactor

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.technical.mobile.domain.model.MobileSettings

interface MobileBackendInteractor {
  suspend fun getMobileSettings(): Either<DomainError, MobileSettings>
}