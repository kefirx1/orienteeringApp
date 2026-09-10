package pl.dev.bkwiatkowski.technical.flags.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.technical.flags.domain.repository.FeatureFlagRepository

interface ClearFlagDataUC : EitherUseCase<UseCase.Params.Empty, Unit>

class ClearFlagDataUCImpl(
  private val featureFlagRepository: FeatureFlagRepository,
) : ClearFlagDataUC {
  override suspend fun invoke(params: UseCase.Params.Empty): Either<DomainError, Unit> =
    featureFlagRepository.clearAllFeatureFlags().onRight {
      Log.i(
        tag = Tag(this),
        message = "Cleared all feature flags successfully",
      )
    }
}