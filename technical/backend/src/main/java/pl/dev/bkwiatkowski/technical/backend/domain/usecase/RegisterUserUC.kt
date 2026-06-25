package pl.dev.bkwiatkowski.technical.backend.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignInResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignUpRequest
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendAuthenticationRepository

interface RegisterUserUC : EitherUseCase<RegisterUserUC.RegisterUserParams, MobileSignInResponse> {
  data class RegisterUserParams(
    val request: MobileSignUpRequest,
  ) : UseCase.Params
}

class RegisterUserUCImpl(
  private val backendAuthenticationRepository: BackendAuthenticationRepository,
) : RegisterUserUC {
  override suspend fun invoke(params: RegisterUserUC.RegisterUserParams): Either<DomainError, MobileSignInResponse> =
    backendAuthenticationRepository.registerUser(params.request)
}

