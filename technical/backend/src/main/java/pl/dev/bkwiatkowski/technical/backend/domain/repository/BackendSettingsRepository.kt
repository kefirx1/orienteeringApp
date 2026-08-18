package pl.dev.bkwiatkowski.technical.backend.domain.repository

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSettingsResponse

interface BackendSettingsRepository {
  suspend fun getMobileSettings(): Either<DomainError, MobileSettingsResponse>
  suspend fun changePassword(
    oldPassword: String,
    newPassword: String,
  ): Either<DomainError, Unit>
}