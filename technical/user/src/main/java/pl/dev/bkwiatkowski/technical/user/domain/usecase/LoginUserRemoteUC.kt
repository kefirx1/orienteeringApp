package pl.dev.bkwiatkowski.technical.user.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.technical.user.domain.interactor.UserBackendInteractor
import pl.dev.bkwiatkowski.technical.user.domain.repository.SessionRepository

interface LoginUserRemoteUC : EitherUseCase<LoginUserRemoteUC.Params, Unit> {
  data class Params(
    val userName: String,
    val password: String,
  ): UseCase.Params
}

class LoginUserRemoteUCImpl(
  private val sessionRepository: SessionRepository,
  private val backendInteractor: UserBackendInteractor,
): LoginUserRemoteUC {
  override suspend fun invoke(params: LoginUserRemoteUC.Params): Either<DomainError, Unit> =
    either {
      val tokenData = backendInteractor.loginUser(
        username = params.userName,
        password = params.password,
      ).getRight()
      Log.i(
        tag = Tag(this@LoginUserRemoteUCImpl),
        message = "Logged in user successfully, got token data",
      )

      sessionRepository.saveNewTokenData(tokenData = tokenData).getRight()
    }
}