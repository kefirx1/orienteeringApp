package pl.dev.bkwiatkowski.technical.user.domain.interactor

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import java.time.LocalDateTime

interface UserBackendInteractor {
  suspend fun registerUser(
    username: String,
    email: String,
    password: String,
    phoneNumber: String?,
    dateOfBirth: LocalDateTime,
  ): Either<DomainError, Unit>
}