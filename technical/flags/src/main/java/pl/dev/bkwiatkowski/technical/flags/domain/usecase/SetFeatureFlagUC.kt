package pl.dev.bkwiatkowski.technical.flags.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.technical.flags.domain.model.FeatureFlag
import pl.dev.bkwiatkowski.technical.flags.domain.repository.FeatureFlagRepository

interface SetFeatureFlagUC : EitherUseCase<SetFeatureFlagUC.SetFeatureFlagParams, Unit> {
  data class SetFeatureFlagParams(
    val flag: FeatureFlag,
    val enabled: Boolean,
  ) : UseCase.Params
}

class SetFeatureFlagUCImpl(
  private val featureFlagRepository: FeatureFlagRepository,
) : SetFeatureFlagUC {
  override suspend fun invoke(params: SetFeatureFlagUC.SetFeatureFlagParams): Either<DomainError, Unit> =
    featureFlagRepository.setFeatureFlagValue(
      flag = params.flag,
      enabled = params.enabled,
    )
}
