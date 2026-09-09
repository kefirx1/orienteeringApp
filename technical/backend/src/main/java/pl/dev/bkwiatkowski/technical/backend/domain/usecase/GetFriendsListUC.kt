package pl.dev.bkwiatkowski.technical.backend.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEGetFriendsListResponse
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendUserRepository

interface GetFriendsListUC : EitherUseCase<UseCase.Params.Empty, BEGetFriendsListResponse>

class GetFriendsListUCImpl(
  private val backendUserRepository: BackendUserRepository,
) : GetFriendsListUC {
  override suspend fun invoke(params: UseCase.Params.Empty): Either<DomainError, BEGetFriendsListResponse> =
    backendUserRepository.getFriendsList()
}
