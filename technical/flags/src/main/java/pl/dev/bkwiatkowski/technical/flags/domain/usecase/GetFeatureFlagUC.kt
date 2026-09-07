package pl.dev.bkwiatkowski.technical.flags.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.technical.flags.domain.model.FeatureFlag
import pl.dev.bkwiatkowski.technical.flags.domain.repository.FeatureFlagRepository

interface GetFeatureFlagUC : EitherUseCase<GetFeatureFlagUC.GetFeatureFlagParams, Boolean> {
  data class GetFeatureFlagParams(val flag: FeatureFlag) : UseCase.Params
}

class GetFeatureFlagUCImpl(
  private val featureFlagRepository: FeatureFlagRepository,
) : GetFeatureFlagUC {
  override suspend fun invoke(params: GetFeatureFlagUC.GetFeatureFlagParams): Either<DomainError, Boolean> =
    featureFlagRepository.isFeatureFlagEnabled(flag = params.flag)
}
