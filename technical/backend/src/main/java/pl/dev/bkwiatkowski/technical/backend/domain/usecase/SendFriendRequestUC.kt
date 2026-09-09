package pl.dev.bkwiatkowski.technical.backend.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendUserRepository

interface SendFriendRequestUC : EitherUseCase<SendFriendRequestUC.Params, Unit> {
  data class Params(val friendId: Int) : UseCase.Params
}

class SendFriendRequestUCImpl(
  private val backendUserRepository: BackendUserRepository,
) : SendFriendRequestUC {
  override suspend fun invoke(params: SendFriendRequestUC.Params): Either<DomainError, Unit> =
    backendUserRepository.sendFriendRequest(friendId = params.friendId)
}
