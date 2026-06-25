package pl.dev.bkwiatkowski.technical.user.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.technical.user.domain.repository.SessionRepository
import kotlin.time.Clock

interface HasValidRefreshTokenUC : EitherUseCase<UseCase.Params.Empty, Boolean>

class HasValidRefreshTokenUCImpl(
  private val sessionRepository: SessionRepository,
) : HasValidRefreshTokenUC {
  override suspend fun invoke(params: UseCase.Params.Empty): Either<DomainError, Boolean> = either {
    val token = sessionRepository.getRefreshToken().getRight()
    val now = Clock.System.now().epochSeconds

    token.expireAtTimestamp > now
  }
}