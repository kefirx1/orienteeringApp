package pl.dev.bkwiatkowski.technical.user.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.technical.user.domain.repository.SessionRepository
import pl.dev.bkwiatkowski.technical.user.domain.repository.UserRepository

interface LogoutUC : EitherUseCase<UseCase.Params.Empty, Unit>

class LogoutUCImpl(
  private val userRepository: UserRepository,
  private val sessionRepository: SessionRepository,
) : LogoutUC {
  override suspend fun invoke(params: UseCase.Params.Empty): Either<DomainError, Unit> = either {
    userRepository.clearUserSettings().getRight()
    sessionRepository.clearAllTokens().getRight()
  }
}
