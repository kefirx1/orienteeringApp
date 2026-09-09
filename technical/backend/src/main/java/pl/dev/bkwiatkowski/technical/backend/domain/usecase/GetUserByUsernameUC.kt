package pl.dev.bkwiatkowski.technical.backend.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.technical.backend.domain.model.BECheckUserResponse
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendUserRepository

interface GetUserByUsernameUC : EitherUseCase<GetUserByUsernameUC.Params, BECheckUserResponse> {
  data class Params(val username: String) : UseCase.Params
}

class GetUserByUsernameUCImpl(
  private val backendUserRepository: BackendUserRepository,
) : GetUserByUsernameUC {
  override suspend fun invoke(params: GetUserByUsernameUC.Params): Either<DomainError, BECheckUserResponse> =
    backendUserRepository.getUserByUsername(username = params.username)
}
