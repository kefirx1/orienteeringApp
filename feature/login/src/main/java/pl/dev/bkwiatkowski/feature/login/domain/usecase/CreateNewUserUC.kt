package pl.dev.bkwiatkowski.feature.login.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.feature.login.domain.interactor.LoginBackendInteractor
import java.time.LocalDateTime

interface CreateNewUserUC : EitherUseCase<CreateNewUserUC.Params, Unit> {
  data class Params(
    val username: String,
    val email: String,
    val password: String,
    val phoneNumber: String?,
    val dateOfBirth: LocalDateTime,
  ): UseCase.Params
}

class CreateNewUserUCImpl(
  private val loginBackendInteractor: LoginBackendInteractor,
) : CreateNewUserUC {
  override suspend fun invoke(params: CreateNewUserUC.Params): Either<DomainError, Unit> = either {
    loginBackendInteractor.registerUser(
      username = params.username,
      email = params.email,
      password = params.password,
      phoneNumber = params.phoneNumber,
      dateOfBirth = params.dateOfBirth,
    ).getRight()
    Log.i(
      tag = Tag(this@CreateNewUserUCImpl),
      message = "Remote user created successfully",
    )


  }
}