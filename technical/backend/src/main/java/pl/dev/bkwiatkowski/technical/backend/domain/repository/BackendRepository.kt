package pl.dev.bkwiatkowski.technical.backend.domain.repository

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignInRequest
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignInResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignUpRequest

interface BackendRepository {
  suspend fun registerUser(
    request: MobileSignUpRequest,
  ): Either<DomainError, MobileSignInResponse>

  suspend fun loginUser(
    request: MobileSignInRequest,
  ): Either<DomainError, MobileSignInResponse>
}