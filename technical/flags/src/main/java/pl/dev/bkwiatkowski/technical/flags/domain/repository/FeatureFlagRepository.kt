package pl.dev.bkwiatkowski.technical.flags.domain.repository

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.technical.flags.domain.model.FeatureFlag

interface FeatureFlagRepository {
  suspend fun isFeatureFlagEnabled(flag: FeatureFlag): Either<DomainError, Boolean>

  suspend fun setFeatureFlagValue(flag: FeatureFlag, enabled: Boolean): Either<DomainError, Unit>

  suspend fun clearAllFeatureFlags(): Either<DomainError, Unit>
}
