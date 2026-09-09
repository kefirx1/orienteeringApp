package pl.dev.bkwiatkowski.technical.backend.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendUserRepository

interface RemoveFriendUC : EitherUseCase<RemoveFriendUC.Params, Unit> {
  data class Params(val friendId: Int) : UseCase.Params
}

class RemoveFriendUCImpl(
  private val backendUserRepository: BackendUserRepository,
) : RemoveFriendUC {
  override suspend fun invoke(params: RemoveFriendUC.Params): Either<DomainError, Unit> =
    backendUserRepository.removeFriend(friendId = params.friendId)
}
