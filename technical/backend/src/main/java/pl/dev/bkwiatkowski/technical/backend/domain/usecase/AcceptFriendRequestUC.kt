package pl.dev.bkwiatkowski.technical.backend.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendUserRepository

interface AcceptFriendRequestUC : EitherUseCase<AcceptFriendRequestUC.Params, Unit> {
  data class Params(val friendId: Int) : UseCase.Params
}

class AcceptFriendRequestUCImpl(
  private val backendUserRepository: BackendUserRepository,
) : AcceptFriendRequestUC {
  override suspend fun invoke(params: AcceptFriendRequestUC.Params): Either<DomainError, Unit> =
    backendUserRepository.acceptFriendRequest(friendId = params.friendId)
}
