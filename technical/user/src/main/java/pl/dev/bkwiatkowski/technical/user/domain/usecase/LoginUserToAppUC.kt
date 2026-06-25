package pl.dev.bkwiatkowski.technical.user.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either

interface LoginUserToAppUC : EitherUseCase<LoginUserToAppUC.Params, Unit> {
  data class Params(
    val userName: String,
    val password: String,
  ): UseCase.Params
}

class LoginUserToAppUCImpl(
  private val loginUserRemoteUC: LoginUserRemoteUC,
  private val loginUserLocalUC: LoginUserLocalUC,
): LoginUserToAppUC {
  override suspend fun invoke(params: LoginUserToAppUC.Params): Either<DomainError, Unit> = either {
    loginUserRemoteUC(
      params = LoginUserRemoteUC.Params(
        userName = params.userName,
        password = params.password,
      )
    ).getRight()

    loginUserLocalUC(params = UseCase.Params.Empty).getRight()
  }
}