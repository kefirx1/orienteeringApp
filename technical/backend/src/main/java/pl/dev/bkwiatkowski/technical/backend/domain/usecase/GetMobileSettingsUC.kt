package pl.dev.bkwiatkowski.technical.backend.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSettingsResponse
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendSettingsRepository

interface GetMobileSettingsUC : EitherUseCase<UseCase.Params.Empty, MobileSettingsResponse>

class GetMobileSettingsUCImpl(
  private val backendSettingsRepository: BackendSettingsRepository,
) : GetMobileSettingsUC {
  override suspend fun invoke(params: UseCase.Params.Empty): Either<DomainError, MobileSettingsResponse> =
    backendSettingsRepository.getMobileSettings()
}