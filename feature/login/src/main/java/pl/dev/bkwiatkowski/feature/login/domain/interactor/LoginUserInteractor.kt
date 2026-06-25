package pl.dev.bkwiatkowski.feature.login.domain.interactor

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import java.time.LocalDateTime

interface LoginUserInteractor {
  suspend fun initMasterKey(): Either<DomainError, Unit>

  suspend fun createNewUser(
    username: String,
    email: String,
    password: String,
    phoneNumber: String?,
    dateOfBirth: LocalDateTime,
  ): Either<DomainError, Unit>

  suspend fun createNewLocalUser(
    username: String,
  ): Either<DomainError, Unit>

  suspend fun loginUserRemote(
    username: String,
    password: String,
  ): Either<DomainError, Unit>

  suspend fun hasValidRefreshToken(): Either<DomainError, Boolean>
}