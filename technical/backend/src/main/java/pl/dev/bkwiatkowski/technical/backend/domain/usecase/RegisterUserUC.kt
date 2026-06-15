package pl.dev.bkwiatkowski.technical.backend.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignUpRequest
import pl.dev.bkwiatkowski.technical.backend.data.repository.BackendRepository

interface RegisterUserUC : EitherUseCase<RegisterUserUC.RegisterUserParams, Unit> {
  data class RegisterUserParams(
    val request: MobileSignUpRequest,
  ) : UseCase.Params
}

class RegisterUserUCImpl(
  private val backendRepository: BackendRepository,
) : RegisterUserUC {
  override suspend fun invoke(params: RegisterUserUC.RegisterUserParams): Either<DomainError, Unit> =
    backendRepository.registerUser(params.request)
}

