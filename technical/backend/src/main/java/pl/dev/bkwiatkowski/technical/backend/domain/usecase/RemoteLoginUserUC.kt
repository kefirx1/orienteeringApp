package pl.dev.bkwiatkowski.technical.backend.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignInRequest
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignInResponse
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendRepository

interface RemoteLoginUserUC : EitherUseCase<RemoteLoginUserUC.Params, MobileSignInResponse> {
  data class Params(
    val request: MobileSignInRequest,
  ) : UseCase.Params
}

class RemoteLoginUserUCImpl(
  private val backendRepository: BackendRepository,
) : RemoteLoginUserUC {
  override suspend fun invoke(params: RemoteLoginUserUC.Params): Either<DomainError, MobileSignInResponse> =
    backendRepository.loginUser(params.request)
}

