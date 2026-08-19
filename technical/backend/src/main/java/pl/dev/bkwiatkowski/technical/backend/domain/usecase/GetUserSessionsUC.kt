package pl.dev.bkwiatkowski.technical.backend.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEUserSession
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendUserRepository

interface GetUserSessionsUC : EitherUseCase<GetUserSessionsUC.Params, List<BEUserSession>> {
  data class Params(val userId: Int) : UseCase.Params
}

class GetUserSessionsUCImpl(
  private val backendUserRepository: BackendUserRepository,
) : GetUserSessionsUC {
  override suspend fun invoke(params: GetUserSessionsUC.Params): Either<DomainError, List<BEUserSession>> =
    backendUserRepository.getUserSessions(userId = params.userId)
}
