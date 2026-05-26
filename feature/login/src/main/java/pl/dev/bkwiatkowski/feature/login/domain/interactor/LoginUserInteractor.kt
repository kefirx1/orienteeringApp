package pl.dev.bkwiatkowski.feature.login.domain.interactor

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either

interface LoginUserInteractor {
  suspend fun getSavedUserName(): Either<DomainError, String>
}