package pl.dev.bkwiatkowski.technical.mobile.domain.repository

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.technical.mobile.domain.model.MobileSettings

interface MobileSettingsRepository {
  suspend fun getMobileSettings(): Either<DomainError, MobileSettings>
  suspend fun saveMobileSettings(settings: MobileSettings): Either<DomainError, Unit>
}