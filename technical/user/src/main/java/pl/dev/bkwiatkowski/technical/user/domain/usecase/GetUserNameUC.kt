package pl.dev.bkwiatkowski.technical.user.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.technical.user.data.repository.UserRepository

interface GetUserNameUC : EitherUseCase<UseCase.Params.Empty, String>

class GetUserNameUCImpl(
  private val userRepository: UserRepository,
) : GetUserNameUC {
  override suspend fun invoke(params: UseCase.Params.Empty): Either<DomainError, String> = either {
    userRepository.getUserSettings().getRight().userName
  }
}
