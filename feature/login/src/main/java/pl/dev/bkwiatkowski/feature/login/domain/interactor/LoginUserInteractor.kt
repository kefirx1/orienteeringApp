package pl.dev.bkwiatkowski.feature.login.domain.interactor

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import java.time.LocalDateTime

interface LoginUserInteractor {
  suspend fun getSavedUserName(): Either<DomainError, String>
  suspend fun createNewUser(
    username: String,
    email: String,
    password: String,
    phoneNumber: String?,
    dateOfBirth: LocalDateTime,
  ): Either<DomainError, Unit>
}