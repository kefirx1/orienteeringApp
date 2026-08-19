package pl.dev.bkwiatkowski.technical.backend.domain.repository

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEUserSession

interface BackendUserRepository {
  suspend fun getUserSessions(userId: Int): Either<DomainError, List<BEUserSession>>
}
